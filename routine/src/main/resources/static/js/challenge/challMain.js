document.addEventListener("DOMContentLoaded", function () {
  //==========================chalMain-right==========================
  const centerDiv = document.querySelector(".challenge-list");

  function loadFragment(url) {
    axios
      .get(url)
      .then(function (response) {
        centerDiv.innerHTML = response.data;

        //조각 불러올 때 스크립트 초기화
        if (centerDiv.querySelector("#new-form")) {
          newChalScript();
        }
      })
      .catch(function (error) {
        console.error("Error loading fragment:", error);
      });
  }

  document
    .getElementById("right-newChal")
    .addEventListener("click", function (e) {
      e.preventDefault();
      loadFragment(this.getAttribute("href"));
    });

  document
    .getElementById("right-createdChal")
    .addEventListener("click", function (e) {
      e.preventDefault();
      loadFragment(this.getAttribute("href"));
    });

  document
    .getElementById("right-joinedChal")
    .addEventListener("click", function (e) {
      e.preventDefault();
      loadFragment(this.getAttribute("href"));
    });
});

//==========================newChal==========================
function newChalScript() {
  /** ---------- Summernote 0.9.1 한국어 초기화 ---------- */
  $("#new-content").summernote({
    placeholder: "챌린지 내용을 입력하세요 (최대 1000자)",
    height: 300,
    width: "100%",
    lang: "ko-KR",
    maximumImageFileSize: 200 * 1024,
    callbacks: {
      onInit: function () {
        $("#new-submit").prop("disabled", false);
      },
      onImageUpload: function (files) {
        handleImages(files, this);
      },
    },
  });

  /** ---------- 이미지 압축 & WebP 변환 ---------- */
  async function compressImage(file) {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement("canvas");
        const max = 1200;
        let { width, height } = img;
        if (width > max || height > max) {
          const ratio = Math.min(max / width, max / height);
          width = Math.round(width * ratio);
          height = Math.round(height * ratio);
        }
        canvas.width = width;
        canvas.height = height;
        canvas.getContext("2d").drawImage(img, 0, 0, width, height);
        canvas.toBlob(
          (blob) => {
            blob.name = file.name.replace(/\.\w+$/, ".webp");
            resolve(blob);
          },
          "image/webp",
          0.85
        );
      };
      img.src = URL.createObjectURL(file);
    });
  }

  async function handleImages(files, editor) {
    const maxCnt = 5;
    const currentCnt = $(editor).summernote("code").match(/<img/g)?.length || 0;
    if (currentCnt + files.length > maxCnt) {
      alert("사진은 최대 5장까지 첨부할 수 있습니다.");
      return;
    }
    for (const f of files) {
      if (!f.type.startsWith("image/")) continue;
      const blob = await compressImage(f);
      if (blob.size > 200 * 1024) {
        alert(`"${f.name}"은(는) 200KB를 초과합니다.`);
        continue;
      }
      const url = URL.createObjectURL(blob);
      $(editor).summernote("insertImage", url, blob.name);
    }
  }

  /** ---------- 썸네일 전용 ---------- */
  $("#new-thumb").on("change", async function () {
    const file = this.files[0];
    if (!file) return;

    //webp로 변환 및 리사이즈
    const blob = await compressImage(file);
    if (blob.size > 200 * 1024) {
      alert("썸네일은 200KB 이하만 허용됩니다.");
      this.value = "";
      return;
    }
    const url = URL.createObjectURL(blob);
    $("#new-thumb-preview").attr("src", url).removeClass("d-none");

    //Base64 변환
    const reader = new FileReader();
    reader.onload = function (e) {
      const base64 = e.target.result.split(",")[1];
      $("#new-thumb-base64").val(base64);
    };
    reader.readAsDataURL(blob);
  });

  $("#new-copy-first").on("click", function () {
    const firstImg = $("#new-content").next(".note-editor").find("img").first();
    if (firstImg.length === 0) {
      alert("첨부한 사진이 없습니다.");
      return;
    }
    $("#new-thumb-preview")
      .attr("src", firstImg.attr("src"))
      .removeClass("d-none");
    fetch(firstImg.attr("src"))
      .then((r) => r.blob())
      .then((b) => {
        const dt = new DataTransfer();
        dt.items.add(new File([b], "thumbnail.webp", { type: "image/webp" }));
        $("#new-thumb")[0].files = dt.files;
      });
  });

  /** ---------- 카테고리 연결 ---------- */
  $("#new-cat-parent").on("change", function () {
    const parent = Number(this.value);
    const childSel = $("#new-cat-child").prop("disabled", false).empty();
    childSel.append('<option value="" selected disabled>세부 선택…</option>');
    catAll
      .filter(
        (c) =>
          Math.floor(c.categoryNo / 10) * 10 === parent &&
          c.categoryNo % 10 !== 0
      )
      .forEach((c) =>
        childSel.append(
          `<option value="${c.categoryNo}">${c.categoryName}</option>`
        )
      );
  });

  /** ---------- 인증 주기 ---------- */
  $("#new-cycle-type").on("change", function () {
    const $opt = $("#new-cycle-options");
    $opt.empty();
    const type = this.value;

    if (type === "day") {
      $opt.html(
        createRadios("verifyCycle", [201, "매일"], [202, "이틀"], [203, "사흘"])
      );
    } else if (type === "week") {
      $opt.html(
        createCheckboxes(
          "verifyCycleBit",
          [1, "월"],
          [2, "화"],
          [4, "수"],
          [8, "목"],
          [16, "금"],
          [32, "토"],
          [64, "일"]
        )
      );
    } else if (type === "weeknum") {
      $opt.html(createRadios("verifyCycle", [211, "매주"], [212, "2주"]));
    } else if (type === "month") {
      $opt.html(createRadios("verifyCycle", [221, "매달"]));
    } else {
      $opt.html(createRadios("verifyCycle", [0, "없음"]));
    }
  });

  function createRadios(name, ...items) {
    return items
      .map(
        ([v, t], index) => `
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="${name}" id="new-${name}-${v}" value="${v}" ${
          index === 0 ? "checked" : ""
        }>
            <label class="form-check-label" for="new-${name}-${v}">${t}</label>
          </div>`
      )
      .join("");
  }

  function createCheckboxes(name, ...items) {
    return items
      .map(
        ([v, t]) => `
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="checkbox" name="${name}" id="new-${name}-${v}" value="${v}">
            <label class="form-check-label" for="new-${name}-${v}">${t}</label>
          </div>`
      )
      .join("");
  }

  /** ---------- Bootstrap Datepicker 초기화 및 검증 ---------- */
  const today = new Date();
  const maxDate = new Date(today);
  maxDate.setDate(maxDate.getDate() + 90); // 3개월 후

  $("#new-start").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
    todayHighlight: true,
    startDate: today,
    endDate: maxDate,
  });

  $("#new-end").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
    todayHighlight: true,
    startDate: today,
    endDate: maxDate,
  });

  // 종료일을 시작일+7일로 자동 보정하는 함수
  function validateEndDate() {
    const startVal = $("#new-start").val();
    const endVal = $("#new-end").val();
    if (!startVal) return;

    const startDate = new Date(startVal);
    const minEndDate = new Date(startDate);
    minEndDate.setDate(minEndDate.getDate() + 7);

    if (endVal) {
      const endDate = new Date(endVal);
      if (endDate < minEndDate) {
        // 종료일이 7일 이내면 자동 보정
        $("#new-end").datepicker("update", minEndDate);
      }
    }
    // 종료일의 최소 선택 가능일도 보정
    $("#new-end").datepicker("setStartDate", minEndDate);
  }

  // 시작일 변경 시 종료일 검증 및 보정
  $("#new-start").on("changeDate", function (e) {
    validateEndDate();
  });

  // 종료일 변경 시에도 검증
  $("#new-end").on("changeDate", function (e) {
    validateEndDate();
  });

  $("#new-start-now").on("click", function () {
    $("#new-start").val("").datepicker("update");
    // 종료일도 초기화
    $("#new-end").datepicker("setStartDate", today);
    $("#new-end").val("").datepicker("update");
  });

  $("#new-end-none").on("click", function () {
    $("#new-end").val("").datepicker("update");
  });

  /** ---------- 사진 필수 → 도용방지 옵션 ---------- */
  $('input[name="pictureRequired"]').on("change", function () {
    $("#new-anti-area").toggleClass("d-none", this.value !== "Y");
    if (this.value !== "Y") $("#new-anti").prop("checked", false);
  });

  /** ---------- Bootstrap Validation ---------- */
  $("#new-form").on("submit", function (e) {
    if (!this.checkValidity()) {
      e.preventDefault();
      e.stopPropagation();
      $(this).addClass("was-validated");
      return;
    }

    //시작일 / 종료일 없으면 지우기
    if (!$("#new-start").val()) $("#new-start").remove();
    if (!$("#new-end").val()) $("#new-end").remove();

    // 유효성 통과 시 로딩 표시
    $("#new-submit-text").addClass("d-none");
    $("#new-submit-spinner").removeClass("d-none");
    $("#new-submit").prop("disabled", true);
  });

  /** ---------- 초기화 확인 ---------- */
  $("#new-reset").on("click", function (e) {
    e.preventDefault();
    if (confirm("모든 입력을 초기화할까요?")) {
      $("#new-form")[0].reset();
      $("#new-thumb-preview").addClass("d-none");
      $("#new-cycle-type").trigger("change");
      $("#new-anti-area").addClass("d-none");
      $("#new-content").summernote("reset");
      $("#new-start").datepicker("update", "");
      $("#new-end").datepicker("update", "");
    }
  });

  // 페이지 로드 시 초기 설정
  $(document).ready(function () {
    $("#new-cycle-type").trigger("change");
  });
}
