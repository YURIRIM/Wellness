document.addEventListener("DOMContentLoaded", function () {
  // 맨 위로 가기 버튼 표시 및 동작
  window.addEventListener("scroll", function () {
    const btn = document.getElementById("main-scroll-top-btn");
    if (window.scrollY > 200) btn.style.display = "block";
    else btn.style.display = "none";
  });
  document.getElementById("main-scroll-top-btn").onclick = function () {
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  leftScript();
  rightTopScript();
});

function leftScript() {
  // 종료/삭제 버튼 이벤트
  document.getElementById("left-close-btn")?.addEventListener("click", () => {
    if (!confirm("정말 종료하시겠습니까?")) return;
    axios
      .get(`${contextPath}/challenge/closeChal`, { params: { chalNo } })
      .then((r) => {
        if (r.data === "success") goback(-1);
        else alert("종료에 실패하였습니다.");
      });
  });
  document.getElementById("left-delete-btn")?.addEventListener("click", () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    axios
      .get(`${contextPath}/challenge/deleteChal`, { params: { chalNo } })
      .then((r) => {
        if (r.data === "success") goback(-1);
        else alert("삭제에 실패하였습니다.");
      });
  });

  // 도넛 차트 그리기
  const ctx = document
    .getElementById("left-participation-chart")
    .getContext("2d");

  const isNoParticipation = participateCount === 0;

  const chartColors = isNoParticipation
    ? ["#6c757d"] // 회색 한 조각
    : ["#007bff", "#28a745", "#dc3545"];

  const chartData = isNoParticipation
    ? [100] // 참가자 없을 때 한 조각만
    : [successRatio, progressRatio, failRatio];

  // 실제 라벨 텍스트 생성 함수
  function participationLabels(chart) {
    if (isNoParticipation) {
      return [
        {
          text: "참여한 사람이 없습니다",
          fillStyle: "#6c757d",
          strokeStyle: "#fff",
          lineWidth: 2,
        },
      ];
    }
    // 기본 label 생성
    return Chart.defaults.plugins.legend.labels.generateLabels(chart);
  }

  new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: isNoParticipation ? [""] : ["성공률", "진행중 비율", "실패율"],
      datasets: [
        {
          data: chartData,
          backgroundColor: chartColors,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        tooltip: {
          enabled: true,
          callbacks: {
            label: (tooltipItem) => {
              if (isNoParticipation) {
                return "참여한 사람이 없습니다";
              }
              const label = tooltipItem.label;
              let count = 0;
              if (label === "성공률") count = successCount;
              if (label === "진행중 비율")
                count = participateCount - successCount - failCount;
              if (label === "실패율") count = failCount;
              return `${label}: ${tooltipItem.parsed}% (${count}명)`;
            },
          },
        },
        legend: {
          display: false,
        },
      },
    },
  });

  //썸네일
  let thumbImgs = document.querySelectorAll("#left-thumbnail");
  if (thumbImgs.length === 2) {
    // 서버에 썸네일 없을 때
    let defaultBase64 = localStorage.getItem("challengeDefaultImage");
    if (defaultBase64) {
      thumbImgs[1].src = defaultBase64.startsWith("data:")
        ? defaultBase64
        : "data:image/webp;base64," + defaultBase64;
      thumbImgs[1].classList.remove("d-none");
    } else {
      // 없을 때는 차선책 (예: no-image.png 등)
      thumbImgs[1].src = "/static/img/no-image.png";
      thumbImgs[1].classList.remove("d-none");
    }
  }

  //작성자
  const writerCard = document.getElementById("left-writer-profile");
  if (writerCard && leftWriterIsOpen === "Y") {
    writerCard.classList.add("cursor-pointer"); // 클릭 가능 UI 강조
    writerCard.addEventListener("click", function () {
      axios
        .get(`${contextPath}/profile/profileDetail`, {
          params: { userNo: leftWriterUserNo },
        })
        .then(function (r) {
          if (r.data && r.data.success) {
            location.href = `${contextPath}/profile/profileDetail?userNo=${leftWriterUserNo}`;
          } else {
            alert("프로필 조회에 실패했습니다.");
          }
        })
        .catch(function () {
          alert("프로필 조회에 실패했습니다.");
        });
    });
  } else if (writerCard) {
    writerCard.classList.remove("cursor-pointer");
    writerCard.style.cursor = "not-allowed"; // 금지 커서 (명확하게)
  }
}

function rightTopScript() {
  const fileInput = document.getElementById("right-top-file-input");
  const thumbContainer = document.getElementById("right-top-thumb-container");
  const submitBtn = document.getElementById("right-top-submit-btn");
  const replyText = document.getElementById("right-top-reply-text");
  const charCount = document.getElementById("right-top-char-count");
  const participateBtn = document.getElementById("right-top-participate-btn");

  // 참여하기
  if (participateBtn) {
    participateBtn.addEventListener("click", () => {
      if (!confirm("챌린지에 참여하시겠습니까?")) return;
      participateBtn.disabled = true;

      const formData = new FormData();
      formData.append("chalNo", chalNo);

      axios
        .post(`${contextPath}/challenge/participate`, formData)
        .then((res) => {
          if (res.data === "success") location.reload();
          else alert("실패했습니다.");
        })
        .catch(() => alert("서버 오류"))
        .finally(() => (participateBtn.disabled = false));
    });
  }

  // 댓글 영역 초기화
  function updateSubmitState() {
    const textOk = replyText
      ? replyText.value.trim().length > 0 || replyRequired === "O"
      : true;
    const picOk = pictureRequired === "N" || uploadedUuid;
    if (loginUserIsParticipation === "Y" && submitBtn) {
      submitBtn.disabled = !(textOk && picOk);
    }
  }

  // 문자 수 카운트
  if (replyText) {
    replyText.addEventListener("input", () => {
      charCount.textContent = `${replyText.value.length}/1000`;
      updateSubmitState();
    });
  }

  // 파일 업로드 처리 및 썸네일 미리보기
  if (fileInput) {
    fileInput.disabled = false;
    fileInput.addEventListener("change", () => {
      const file = fileInput.files[0];
      if (!file) return;
      // 썸네일 미리보기: 업로드 시도 파일 바로 보여줌
      const reader = new FileReader();
      reader.onload = function (e) {
        thumbContainer.innerHTML = `<img src="${e.target.result}" class="img-thumbnail" style="max-width:100px; max-height:100px;">`;
      };
      reader.readAsDataURL(file);

      // 리사이즈 및 용량 체크
      resizeImage(file)
        .then((blob) => {
          const form = new FormData();
          form.append("file", blob, "resized_image.webp");
          form.append("chalNo", chalNo);
          submitBtn.disabled = true;
          return axios.post(`${contextPath}/attachment/insertComment`, form);
        })
        .then((res) => {
          if (res.data === "joongBock") {
            new bootstrap.Modal(
              document.getElementById("duplicationModal")
            ).show();
          } else if (res.data === "fail") {
            alert("서버 오류");
          } else {
            uploadedUuid = res.data;
            updateSubmitState();
          }
        })
        .catch(() => alert("업로드 실패"))
        .finally(() => updateSubmitState());
    });
  }

  // 댓글 제출
  if (submitBtn) {
    submitBtn.addEventListener("click", () => {
      submitBtn.disabled = true;
      const formData = new FormData();
      formData.append("chalNo", chalNo);
      if (uploadedUuid) formData.append("uuidStr", uploadedUuid);
      if (replyText) formData.append("reply", replyText.value.trim());

      axios
        .post(`${contextPath}/chalComment/insertComment`, formData)
        .then((res) => {
          if (res.data === "success") {
            replyText.value = "";
            thumbContainer.innerHTML = "";
            uploadedUuid = null;
            if (fileInput) fileInput.value = "";
            if (charCount) charCount.textContent = "0/1000";
            updateSubmitState();
            // 댓글 목록 새로고침
            axios
              .get(`${contextPath}/chalComment/selectComment`, {
                params: { currentPage: 0 },
              })
              .then((listRes) => {
                const event = new CustomEvent("commentsUpdated", {
                  detail: listRes.data,
                });
                window.dispatchEvent(event);
              });
          } else {
            alert("댓글 등록 실패");
          }
        })
        .catch(() => alert("서버 오류"))
        .finally(() => updateSubmitState());
    });
  }
}

// 이미지 리사이즈
function resizeImage(file) {
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
          resolve(blob);
        },
        "image/webp",
        0.85
      );
    };
    img.src = URL.createObjectURL(file);
  });
}
