//==========================chalMain-right==========================
document.addEventListener("DOMContentLoaded", function () {
  const rightContainer = document.querySelector("#main-right");
  if (!loginUserNo) {
    return;
  }

  // '프로필 생성 영역' 선택
  const profileSection = rightContainer.querySelector(
    "#right-chal-right .list-group"
  );
  if (!profileSection) {
    console.error("프로필 생성 영역을 찾을 수 없습니다.");
    return;
  }

  // 프로필 컨테이너 생성
  const profileDiv = document.createElement("div");
  profileDiv.id = "profile-info";
  profileDiv.classList.add("p-3", "border", "mb-3");

  if (!myProfile) {
    // 프로필이 없으면 '프로필 추가하기' 버튼만 렌더링
    const addBtn = document.createElement("button");
    addBtn.type = "button";
    addBtn.className = "btn btn-primary w-100";
    addBtn.textContent = "프로필 추가하기";
    addBtn.addEventListener("click", () => {
      window.location.href = `${contextPath}/profile/insertMyProfile`;
    });
    profileDiv.appendChild(addBtn);
    profileSection.parentNode.insertBefore(profileDiv, profileSection);
    return;
  }

  // 프로필 사진 영역
  const picDiv = document.createElement("div");
  picDiv.style.textAlign = "center";
  picDiv.style.height = "104px"; // 이미지 100px + 약간 padding
  picDiv.style.display = "flex";
  picDiv.style.justifyContent = "center";
  picDiv.style.alignItems = "center";

  if (
    typeof myProfile.pictureBase64 === "string" &&
    myProfile.pictureBase64.length > 0
  ) {
    const img = document.createElement("img");
    img.alt = "profile picture";
    img.width = 100;
    img.height = 100;
    img.style.objectFit = "cover";
    img.style.borderRadius = "50%";
    img.src = "data:image/webp;base64," + myProfile.pictureBase64;
    picDiv.appendChild(img);
  } else {
    // 프로필 사진 미존재 시, 부트스트랩 아이콘(svg) 사용
    const icon = document.createElement("i");
    icon.className = "bi bi-person-circle fs-1 text-secondary";
    icon.style.fontSize = "70px";
    icon.title = "프로필 사진 없음";
    picDiv.appendChild(icon);
  }
  profileDiv.appendChild(picDiv);

  // 닉네임, bio
  const nickEl = document.createElement("h5");
  nickEl.className = "mt-2 text-center";
  nickEl.textContent = myProfile.nick || loginUserNick || "";
  profileDiv.appendChild(nickEl);

  if (myProfile.bio) {
    const bioEl = document.createElement("p");
    bioEl.className = "text-center text-muted small";
    bioEl.textContent = myProfile.bio;
    profileDiv.appendChild(bioEl);
  }

  // 공개 상태 및 워터마크 뱃지
  const statusEl = document.createElement("div");
  statusEl.className = "d-flex justify-content-center gap-2 my-2 flex-wrap";

  let openText = "";
  let openColor = "";
  switch (myProfile.isOpen) {
    case "Y":
      openText = "공개";
      openColor = "primary";
      break;
    case "N":
      openText = "참여 챌린지 비공개";
      openColor = "secondary";
      break;
    case "A":
      openText = "익명";
      openColor = "warning";
      break;
    default:
      openText = "-";
      openColor = "light";
  }
  const openBadge = `<span class="badge rounded-pill bg-${openColor} px-3 py-2">
    <i class="bi bi-eye${
      myProfile.isOpen === "A" ? "-slash" : ""
    } me-1"></i>${openText}
  </span>`;

  let wmText = "";
  let wmColor = "";
  switch (myProfile.watermarkType) {
    case "D":
      wmText = "디폴트";
      wmColor = "info";
      break;
    case "F":
      wmText = "개인";
      wmColor = "success";
      break;
    case "N":
      wmText = "없음";
      wmColor = "light";
      break;
    default:
      wmText = "-";
      wmColor = "light";
  }
  const wmBadge = `<span class="badge rounded-pill bg-${wmColor} px-3 py-2">
    <i class="bi bi-droplet-half me-1"></i>${wmText}
  </span>`;

  statusEl.innerHTML = openBadge + wmBadge;
  profileDiv.appendChild(statusEl);

  // 참여 현황 다이어그램
  const total = myProfile.chalParticiapteCount;
  if (total > 0) {
    const successPct = Math.floor((myProfile.successRatio || 0) * 100);
    const failPct = Math.floor((myProfile.failRatio || 0) * 100);
    const otherPct = 100 - successPct - failPct;

    const barContainer = document.createElement("div");
    barContainer.className = "progress";
    barContainer.style.height = "16px";
    barContainer.style.cursor = "pointer";

    const makeBar = (color, pct, count) => {
      const seg = document.createElement("div");
      seg.className = "progress-bar";
      seg.style.width = pct + "%";
      seg.style.backgroundColor = color;
      seg.setAttribute("data-count", count);
      seg.setAttribute("title", count);
      seg.addEventListener("mouseenter", (e) => {
        e.target.textContent = `${count}`;
      });
      seg.addEventListener("mouseleave", (e) => {
        e.target.textContent = "";
      });
      return seg;
    };

    barContainer.appendChild(
      makeBar("#0d6efd", successPct, myProfile.successCount)
    );
    barContainer.appendChild(
      makeBar(
        "#6c757d",
        otherPct,
        total - myProfile.successCount - myProfile.failCount
      )
    );
    barContainer.appendChild(makeBar("#dc3545", failPct, myProfile.failCount));

    profileDiv.appendChild(barContainer);
  }

  // 프로필 수정 버튼
  const editBtn = document.createElement("button");
  editBtn.type = "button";
  editBtn.className = "btn btn-outline-secondary w-100 mt-2";
  editBtn.textContent = "프로필 수정";
  editBtn.addEventListener("click", () => {
    window.location.href = `${contextPath}/profile/updateMyProfile`;
  });
  profileDiv.appendChild(editBtn);

  // 렌더링 위치: 프로필 생성 영역 바로 위
  profileSection.parentNode.insertBefore(profileDiv, profileSection);

  //==========================버튼 맵핑==========================
  const centerTarget = document.querySelector("#main-challenge-list");

  function loadCenterFragment(url, callback) {
    axios
      .get(contextPath + url, {
        headers: { "X-Requested-With": "XMLHttpRequest" },
        responseType: "text", // 텍스트 응답 받기
      })
      .then((res) => {
        centerTarget.innerHTML = res.data;
        window.scrollTo({ top: 0, behavior: "smooth" });
        if (typeof callback === "function") {
          callback();
        }
      })
      .catch((err) => {
        console.error("중앙 조각 로딩 실패", err);
        alert("콘텐츠를 불러오는 중 오류가 발생했습니다.");
      });
  }

  // 새로운 챌린지 생성하기
  document
    .getElementById("right-newChal")
    ?.addEventListener("click", function (e) {
      e.preventDefault();
      loadCenterFragment("/challenge/newChal", newChalScript);
    });

  // 내가 생성한 챌린지
  document
    .getElementById("right-createdChal")
    ?.addEventListener("click", function (e) {
      e.preventDefault();
      loadCenterFragment("/challenge/goMyChal?searchType=O", myChalScript);
    });

  // 내가 참여한 챌린지
  document
    .getElementById("right-joinedChal")
    ?.addEventListener("click", function (e) {
      e.preventDefault();
      loadCenterFragment("/challenge/goMyChal?searchType=J", myChalScript);
    });
});

//==========================chalMain-left==========================
function chalMainLeftScript() {
  // 뒤로가기 버튼
  document
    .getElementById("left-back-btn")
    .addEventListener("click", function () {
      location.href = contextPath;
    });

  // 세부 검색 버튼
  document
    .getElementById("left-detail-search-btn")
    .addEventListener("click", function () {
      const detailArea = document.getElementById("left-detail-search-area");
      const btn = document.getElementById("left-detail-search-btn");
      const icon = btn.querySelector("i");

      if (detailArea.style.display === "none") {
        detailArea.style.display = "block";
        icon.className = "bi bi-chevron-up";
        btn.innerHTML = '<i class="bi bi-chevron-up"></i> 세부 검색 닫기';
      } else {
        detailArea.style.display = "none";
        icon.className = "bi bi-chevron-down";
        btn.innerHTML = '<i class="bi bi-chevron-down"></i> 세부 검색';
      }
    });

  // 카테고리 부모 선택 시 자식 카테고리 업데이트
  document
    .getElementById("left-category-parent")
    .addEventListener("change", function () {
      const parent = parseInt(this.value, 10);
      const childSel = document.getElementById("left-category-child");

      // 1) 초기화 - "전체" 옵션의 value는 0으로 설정
      childSel.disabled = parent === 0;
      childSel.innerHTML = '<option value="0">전체</option>';

      if (parent === 0) return; // 전체 선택이면 종료

      // 2) catAll에서 자식 카테고리 필터링 (window.catAll 대신 catAll 직접 사용)
      const children = (catAll || []).filter((c) => {
        const categoryNo = parseInt(c.categoryNo, 10);
        const parentCategory = Math.floor(categoryNo / 10) * 10;
        const isChild = categoryNo % 10 !== 0;

        return parentCategory === parent && isChild;
      });

      // 3) 옵션 동적 추가
      children.forEach((c) => {
        const opt = document.createElement("option");
        opt.value = c.categoryNo;
        opt.textContent = c.categoryName;
        childSel.appendChild(opt);
      });
    });

  // 인증 주기 선택 처리
  document
    .getElementById("left-cycle-type")
    .addEventListener("change", function () {
      const opt = document.getElementById("left-cycle-options");
      const verifyCycleInput = document.getElementById("left-verify-cycle");

      opt.innerHTML = "";
      const type = this.value;

      // 기본값으로 초기화
      verifyCycleInput.value = "0";

      if (type === "day") {
        opt.innerHTML = createRadios(
          "verifyCycleRadio",
          [201, "매일"],
          [202, "이틀"],
          [203, "사흘"]
        );
      } else if (type === "week") {
        opt.innerHTML = createCheckboxes(
          "verifyCycleCheck",
          [1, "월"],
          [2, "화"],
          [4, "수"],
          [8, "목"],
          [16, "금"],
          [32, "토"],
          [64, "일"]
        );
      } else if (type === "weeknum") {
        opt.innerHTML = createRadios(
          "verifyCycleRadio",
          [211, "매주"],
          [212, "2주"]
        );
      } else if (type === "month") {
        opt.innerHTML = createRadios("verifyCycleRadio", [221, "매달"]);
      }
    });

  // 라디오 버튼 생성 함수
  function createRadios(name, ...options) {
    let html = "";
    options.forEach((option, index) => {
      const checked = index === 0 ? "checked" : "";
      html += `
        <div class="form-check">
          <input class="form-check-input" type="radio" name="${name}" value="${option[0]}" 
                 id="left-${name}-${option[0]}" ${checked} 
                 onchange="updateVerifyCycle(${option[0]})">
          <label class="form-check-label" for="left-${name}-${option[0]}">${option[1]}</label>
        </div>
      `;
    });
    return html;
  }

  // 체크박스 생성 함수
  function createCheckboxes(_, ...options) {
    let html = "";
    options.forEach((opt) => {
      html += `
        <div class="form-check form-check-inline">
          <input class="form-check-input verifyCycleChk"
                 type="checkbox"
                 value="${opt[0]}"
                 id="left-vc-${opt[0]}"
                 onchange="updateVerifyCycleFromCheckbox()">
          <label class="form-check-label" for="left-vc-${opt[0]}">${opt[1]}</label>
        </div>
      `;
    });
    return html;
  }

  // verifyCycle 업데이트 함수
  window.updateVerifyCycle = function (value) {
    document.getElementById("left-verify-cycle").value = value;
  };

  // 체크박스에서 verifyCycle 업데이트 함수
  window.updateVerifyCycleFromCheckbox = function () {
    const bitSum = [
      ...document.querySelectorAll(".verifyCycleChk:checked"),
    ].reduce((sum, cb) => sum + parseInt(cb.value, 10), 0);
    document.getElementById("left-verify-cycle").value = bitSum;
  };

  // 날짜 필드에 시간 부분 추가하는 함수
  function formatDateTimeString(dateString) {
    if (!dateString || dateString.trim() === "") {
      return "1453-05-29 00:00:00";
    }
    return dateString + " 00:00:00";
  }

  // 검색 버튼 클릭 처리 (수정: 날짜 형식 처리)
  document
    .getElementById("left-search-btn")
    .addEventListener("click", function (e) {
      e.preventDefault();
      const form = document.getElementById("left-search-form");

      // FormData로 전체 폼 데이터 추출
      const params = {};
      for (const [key, value] of new FormData(form)) {
        params[key] = value;
      }

      // 날짜 필드: 시간 부분 추가
      ["startDate1", "startDate2", "endDate1", "endDate2"].forEach((field) => {
        const input = document.querySelector(`[name="${field}"]`);
        if (input) {
          params[field] = formatDateTimeString(input.value);
        }
      });

      axios
        .get(contextPath + "/challenge/chalMainSearchLeft", { params })
        .then(function (response) {
          document.getElementById("main-challenge-list").innerHTML =
            response.data;
        })
        .catch(function (error) {
          console.error("검색 중 오류 발생:", error);
          alert("검색 중 오류가 발생했습니다.");
        });
    });

  // 초기화 버튼 클릭 처리 (수정: verifyCycleBit 제거)
  document
    .getElementById("left-reset-btn")
    .addEventListener("click", function () {
      const form = document.getElementById("left-search-form");
      form.reset();

      // 세부 검색 영역 닫기
      document.getElementById("left-detail-search-area").style.display = "none";
      document.getElementById("left-detail-search-btn").innerHTML =
        '<i class="bi bi-chevron-down"></i> 세부 검색';

      // 인증 주기 옵션 초기화
      document.getElementById("left-cycle-options").innerHTML = "";

      // 카테고리 자식 선택박스 초기화
      const childSelect = document.getElementById("left-category-child");
      childSelect.innerHTML = '<option value="0">전체</option>';
      childSelect.disabled = true;

      // hidden input 초기화
      document.getElementById("left-verify-cycle").value = "0";
    });
}

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

  /** ---------- 썸네일 ---------- */
  $("#new-thumb").on("change", async function () {
    const file = this.files[0];
    if (!file) return;

    const blob = await compressImage(file);
    if (blob.size > 200 * 1024) {
      alert("썸네일은 200KB 이하만 허용됩니다.");
      this.value = "";
      return;
    }

    const url = URL.createObjectURL(blob);
    $("#new-thumb-preview").attr("src", url).removeClass("d-none");

    const reader = new FileReader();
    reader.onload = function (e) {
      const base64 = e.target.result.split(",")[1];
      const safeBase64 = base64
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "");
      $("#new-thumb-base64").val(safeBase64);
    };
    reader.readAsDataURL(blob);
  });

  $("#new-copy-first").on("click", async function () {
    const firstImg = $("#new-content").next(".note-editor").find("img").first();
    if (firstImg.length === 0) {
      alert("첨부한 사진이 없습니다.");
      return;
    }

    const imageUrl = firstImg.attr("src");
    const resp = await fetch(imageUrl);
    const blob = await resp.blob();

    const thumbUrl = URL.createObjectURL(blob);
    $("#new-thumb-preview").attr("src", thumbUrl).removeClass("d-none");

    const reader = new FileReader();
    reader.onload = function (e) {
      const base64 = e.target.result.split(",")[1];
      const safeBase64 = base64
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "");
      $("#new-thumb-base64").val(safeBase64);
    };
    reader.readAsDataURL(blob);
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
          <input type="radio" class="form-check-input" name="${name}" value="${v}" id="new-${name}-${v}" ${
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

  /** ---------- Bootstrap Datepicker ---------- */
  const today = new Date();
  const maxDate = new Date(today);
  maxDate.setDate(maxDate.getDate() + 90);

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
        $("#new-end").datepicker("update", minEndDate);
      }
    }
    $("#new-end").datepicker("setStartDate", minEndDate);
  }

  $("#new-start").on("changeDate", validateEndDate);
  $("#new-end").on("changeDate", validateEndDate);

  $("#new-start-now").on("click", function () {
    $("#new-start").val("").datepicker("update");
    $("#new-end")
      .datepicker("setStartDate", today)
      .val("")
      .datepicker("update");
  });

  $("#new-end-none").on("click", function () {
    $("#new-end").val("").datepicker("update");
  });

  /** ---------- 사진 필수 → 도용방지 옵션 ---------- */
  $('input[name="pictureRequired"]').on("change", function () {
    $("#new-anti-area").toggleClass("d-none", this.value !== "Y");
    if (this.value !== "Y") $("#new-anti").prop("checked", false);
  });

  /** ---------- 폼 제출 전 검증 처리 ---------- */
  $("#new-form").on("submit", function (e) {
    if (!this.checkValidity()) {
      e.preventDefault();
      e.stopPropagation();
      $(this).addClass("was-validated");
      return;
    }

    // 비트마스크 처리
    const type = $("#new-cycle-type").val();
    if (type === "week") {
      const bits = $("input[name='verifyCycleBit']:checked")
        .map(function () {
          return parseInt(this.value);
        })
        .get();
      const sum = bits.reduce((a, b) => a + b, 0);
      $("input[name='verifyCycleBit']").remove();
      $("<input>")
        .attr({ type: "hidden", name: "verifyCycle", value: sum })
        .appendTo("#new-form");
    }

    // 날짜가 비었으면 name 자체 제거
    if (!$("#new-start").val()) $("#new-start").removeAttr("name");
    if (!$("#new-end").val()) $("#new-end").removeAttr("name");

    // 로딩 처리
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

  $(document).ready(function () {
    $("#new-cycle-type").trigger("change");
  });
}

//==========================myChal(createdChal/joinedChal)==========================
function myChalScript() {}

// 조각이 로드될 때 스크립트 실행
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", chalMainLeftScript);
} else {
  chalMainLeftScript();
}
