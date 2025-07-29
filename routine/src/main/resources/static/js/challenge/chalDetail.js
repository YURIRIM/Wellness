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
  rightBottomScript();
});

function leftScript() {
  // 종료/삭제 버튼 이벤트
  document.getElementById("left-close-btn")?.addEventListener("click", () => {
    if (!confirm("정말 종료하시겠습니까?")) return;
    axios
      .get(`${contextPath}/challenge/closeChal`, { params: { chalNo } })
      .then((r) => {
        if (r.data === "success") history.back();
        else alert("종료에 실패하였습니다.");
      });
  });
  document.getElementById("left-delete-btn")?.addEventListener("click", () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    axios
      .get(`${contextPath}/challenge/deleteChal`, { params: { chalNo } })
      .then((r) => {
        if (r.data === "success") history.back();
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
      thumbImgs[1].src =
        contextPath + "/attachment/defaulImg/thumbnail-default.webp";
      thumbImgs[1].classList.remove("d-none");
    }
  }

  //작성자
  const writerCard = document.getElementById("left-writer-profile");
  if (writerCard && leftWriterIsOpen === "Y") {
    writerCard.classList.add("cursor-pointer"); // 클릭 가능 UI 강조
    writerCard.addEventListener("click", function () {
      // 비동기 API 호출 없이 바로 이동
      location.href = `${contextPath}/profile/detail?userNo=${leftWriterUserNo}`;
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
  let selectedStatus = null;
  const successBtn = document.getElementById("btn-success");
  const failBtn = document.getElementById("btn-fail");
  const modal = new bootstrap.Modal(
    document.getElementById("status-confirm-modal")
  );
  const modalBody = document.getElementById("status-modal-body");
  const confirmBtn = document.getElementById("modal-confirm-btn");

  if (successBtn) {
    successBtn.addEventListener("click", function () {
      selectedStatus = "S";
      modalBody.textContent = "성공할래요?";
      modal.show();
    });
  }
  if (failBtn) {
    failBtn.addEventListener("click", function () {
      selectedStatus = "F";
      modalBody.textContent = "실패할래요?";
      modal.show();
    });
  }

  //성공/실패
  confirmBtn.addEventListener("click", function () {
    if (!selectedStatus) return;
    axios
      .get(contextPath + "/chalParticipation/update", {
        params: {
          chalNo: chalNo,
          status: selectedStatus,
        },
      })
      .then(function (response) {
        if (response.status === 200) {
          location.reload();
        } else {
          alert("처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }
      })
      .catch(function (error) {
        alert("처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
      });
    modal.hide();
    selectedStatus = null;
  });

  // 참여하기
  if (participateBtn) {
    participateBtn.addEventListener("click", () => {
      if (!confirm("챌린지에 참여하시겠습니까?")) return;
      participateBtn.disabled = true;

      const formData = new FormData();
      formData.append("chalNo", chalNo);

      axios
        .post(`${contextPath}/chalParticipation/insert`, formData)
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
    let textOk = replyText
      ? replyText.value.trim().length > 0 || replyRequired === "O"
      : true;
    let picOk =
      pictureRequired === "N" || pictureRequired === "O" || !!uploadedUuid;
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

      // 썸네일 미리보기
      const reader = new FileReader();
      reader.onload = function (e) {
        thumbContainer.innerHTML = `<img src="${e.target.result}" class="img-thumbnail" style="max-width:100px; max-height:100px;">`;
      };
      reader.readAsDataURL(file);

      // 공통 업로드 함수 사용
      handleFileUpload(file, {
        onSuccess: (uuid) => {
          uploadedUuid = uuid;
          updateSubmitState();
        },
        onError: () => {
          // 추가 에러 처리가 필요하면 여기에
        },
        onFinally: () => {
          updateSubmitState();
        },
        onProgress: (inProgress) => {
          if (submitBtn) {
            submitBtn.disabled = inProgress;
          }
        },
      });
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

  updateSubmitState();
}

function rightBottomScript() {
  /* 전역 상태 */
  let currentPage = 0;
  let hasMoreComments = true;
  const commentListEl = document.getElementById("right-top-comment-list");
  const loadingEl = document.getElementById("right-top-loading");
  const sentinelEl = document.getElementById("right-top-sentinel");
  const imgModalEl = document.getElementById("right-top-img-modal");
  const modalImgEl = document.getElementById("right-top-modal-img");
  const observer = new IntersectionObserver(onIntersect, { threshold: 0.3 });

  /* 유틸 */
  const timeFmt = new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
  function showLoading(show) {
    loadingEl.style.display = show ? "block" : "none";
  }

  /* 댓글 카드 생성 */
  function createCommentCard(c, imagesMap, isChild = false) {
    const card = document.createElement("div");
    card.className = `card ${isChild ? "ms-5" : ""}`;
    card.dataset.commentNo = c.commentNo;
    const body = document.createElement("div");
    body.className = "card-body";

    if (c.status === "N") {
      body.innerHTML = `<span class="text-muted">삭제된 댓글이네요</span>`;
      card.append(body);
      return card;
    }

    // 프로필·닉네임
    const header = document.createElement("div");
    header.className = "d-flex align-items-center mb-2";
    if (c.isOpen !== "A") {
      const imgSrc = c.pictureBase64
        ? `data:image/webp;base64,${c.pictureBase64}`
        : contextPath + "/attachment/defaultImg/profile-default.webp";
      const profileImg = document.createElement("img");
      profileImg.src = imgSrc;
      profileImg.alt = "프로필";
      profileImg.className = "rounded-circle me-2";
      profileImg.style.width = profileImg.style.height = "40px";
      profileImg.style.cursor = "pointer";
      profileImg.addEventListener("click", () => {
        axios
          .get(`${contextPath}/profile/profileDetail`, {
            params: { userNo: c.userNo },
          })
          .then((res) => (location.href = res.request.responseURL))
          .catch(() => alert("프로필을 불러오지 못했습니다."));
      });
      header.append(profileImg);
      const nick = document.createElement("strong");
      nick.style.cursor = "pointer";
      nick.textContent = c.nick;
      nick.addEventListener("click", () => profileImg.click());
      header.append(nick);
    } else {
      const anon = document.createElement("span");
      anon.className = "text-muted";
      anon.textContent = "익명의 ROUTINE유저가 작성했어요";
      header.append(anon);
    }
    body.append(header);

    // 첨부 이미지
    if (c.hasAttachment === "Y" && imagesMap.has(c.commentNo)) {
      const img = document.createElement("img");
      img.src = imagesMap.get(c.commentNo);
      img.alt = "댓글 이미지";
      img.className = "img-fluid rounded mb-2";
      img.style.maxHeight = "240px";
      img.style.cursor = "pointer";
      img.addEventListener("click", () => {
        modalImgEl.src = img.src;
        new bootstrap.Modal(imgModalEl).show();
      });
      body.append(img);
    }

    // 본문 텍스트
    if (c.reply) {
      const p = document.createElement("p");
      p.className = "card-text mb-2";
      p.textContent = c.reply;
      body.append(p);
    }

    // 날짜·수정 표시
    const footer = document.createElement("div");
    footer.className = "d-flex justify-content-between small text-muted";
    footer.innerHTML = `<span>${timeFmt.format(new Date(c.createDate))}${
      c.status === "M" ? " (수정됨)" : ""
    }</span>`;
    body.append(footer);

    // 수정/삭제 버튼 (본인 댓글일 경우)
    if (loginUser && loginUser.userNo === c.userNo) {
      const grp = document.createElement("div");
      grp.className = "d-flex gap-2 mt-2";
      const btnEdit = document.createElement("button");
      btnEdit.className = "btn btn-outline-secondary btn-sm flex-fill";
      btnEdit.innerHTML = '<i class="bi bi-pencil-square me-1"></i>수정';
      btnEdit.addEventListener("click", () => openEditArea(card, c));
      const btnDel = document.createElement("button");
      btnDel.className = "btn btn-outline-danger btn-sm flex-fill";
      btnDel.innerHTML = '<i class="bi bi-trash3 me-1"></i>삭제';
      btnDel.addEventListener("click", () => handleDelete(c.commentNo));
      grp.append(btnEdit, btnDel);
      body.append(grp);
    }

    // 대댓글 버튼
    if (!isChild) {
      const btnReply = document.createElement("button");
      btnReply.className = "btn btn-link p-0 mt-2";
      btnReply.innerHTML = '<i class="bi bi-chat-dots me-1"></i>대댓글 작성';
      btnReply.addEventListener("click", () =>
        openRecommentArea(card, c.commentNo)
      );
      body.append(btnReply);
    }

    card.append(body);
    return card;
  }

  /* 댓글/대댓글 요청 & 렌더 */
  async function fetchComments(page, isRecomment = false, parentNo = null) {
    if (!hasMoreComments) return;
    showLoading(true);

    const url = isRecomment
      ? `${contextPath}/chalComment/selectRecomment`
      : `${contextPath}/chalComment/selectComment`;
    const params = { chalNo };
    if (isRecomment) {
      params.recommentTarget = parentNo;
      params.currentRecommentPage = page;
    } else {
      params.currentPage = page;
    }

    try {
      const res = await axios.get(url, { params });
      const comments = res.data;
      if (!Array.isArray(comments) || comments.length === 0) {
        hasMoreComments = false;
        return;
      }

      // 첨부 이미지 ZIP 요청
      const attachNos = comments
        .filter((c) => c.hasAttachment === "Y")
        .map((c) => c.commentNo);
      const imagesMap = new Map();
      if (attachNos.length) {
        const zipRes = await axios.post(
          `${contextPath}/attachment/selectComment`,
          attachNos,
          { responseType: "arraybuffer" }
        );
        const zip = await JSZip.loadAsync(zipRes.data);
        await Promise.all(
          Object.keys(zip.files)
            .filter((n) => n.endsWith(".webp"))
            .map(async (n) => {
              const no = +n.split("_")[0];
              const data = await zip.file(n).async("uint8array");
              imagesMap.set(
                no,
                URL.createObjectURL(new Blob([data], { type: "image/webp" }))
              );
            })
        );
      }

      // 렌더: 부모 → 자식
      comments
        .filter((c) => c.recommentTarget === 0)
        .forEach((parent) => {
          commentListEl.append(createCommentCard(parent, imagesMap, false));
          comments
            .filter((c) => c.recommentTarget === parent.commentNo)
            .forEach((child) => {
              commentListEl.append(createCommentCard(child, imagesMap, true));
            });
        });

      if (!isRecomment) currentPage = page + 1;
    } catch (err) {
      console.error(err);
      alert("댓글을 불러오는 중 오류가 발생했습니다.");
    } finally {
      showLoading(false);
    }
  }

  /* IntersectionObserver 콜백 */
  function onIntersect(entries) {
    if (entries[0].isIntersecting && hasMoreComments) {
      observer.unobserve(sentinelEl);
      fetchComments(currentPage).then(() => {
        if (hasMoreComments) observer.observe(sentinelEl);
      });
    }
  }

  /* 댓글 삭제 처리 */
  function handleDelete(commentNo) {
    if (!confirm("댓글을 삭제하시겠습니까?")) return;
    const form = new FormData();
    form.append("commentNo", commentNo);
    axios
      .post(`${contextPath}/chalComment/deleteComment`, form)
      .then((res) => {
        if (res.data === "success") {
          const card = commentListEl.querySelector(
            `[data-comment-no="${commentNo}"]`
          );
          if (card) {
            card.querySelector(".card-body").innerHTML =
              '<span class="text-muted">삭제된 댓글이네요</span>';
          }
        } else alert("삭제 실패");
      })
      .catch(() => alert("서버 오류"));
  }

  /* 댓글 수정 영역 열기 */
  function openEditArea(card, comment) {
    if (card.querySelector(".right-top-edit-area")) {
      card.querySelector(".right-top-edit-area").classList.toggle("d-none");
      return;
    }

    // 컨테이너
    const div = document.createElement("div");
    div.className = "right-top-edit-area mt-3";

    // textarea
    const textarea = document.createElement("textarea");
    textarea.className = "form-control mb-2";
    textarea.maxLength = 1000;
    textarea.rows = 3;
    textarea.value = comment.reply || "";
    div.appendChild(textarea);

    // thumb container
    const thumbContainer = document.createElement("div");
    thumbContainer.className = "mb-2";
    if (comment.hasAttachment === "Y" && comment.attachmentUrl) {
      thumbContainer.innerHTML = `
      <img src="${comment.attachmentUrl}"
           class="img-thumbnail"
           style="max-width:100px; max-height:100px;">
    `;
    }
    div.appendChild(thumbContainer);

    // file input
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.accept = "image/*";
    fileInput.className = "form-control mb-2";
    div.appendChild(fileInput);

    let editUploadedUuid = null;
    let uploadInProgress = false;

    function updateEditSubmitState() {
      btnSave.disabled = uploadInProgress;
    }

    fileInput.addEventListener("change", () => {
      const file = fileInput.files[0];
      if (!file) return;

      // 미리보기
      const reader = new FileReader();
      reader.onload = (e) => {
        thumbContainer.innerHTML = `
        <img src="${e.target.result}"
             class="img-thumbnail"
             style="max-width:100px; max-height:100px;">
      `;
      };
      reader.readAsDataURL(file);

      // 공통 업로드 함수 사용
      handleFileUpload(file, {
        onSuccess: (uuid) => {
          editUploadedUuid = uuid;
        },
        onError: () => {
          // 추가 에러 처리가 필요하면 여기에
        },
        onFinally: () => {
          updateEditSubmitState();
        },
        onProgress: (inProgress) => {
          uploadInProgress = inProgress;
          updateEditSubmitState();
        },
      });
    });

    // 버튼 그룹
    const grp = document.createElement("div");
    grp.className = "d-flex gap-2";

    // 취소
    const btnCancel = document.createElement("button");
    btnCancel.className = "btn btn-outline-secondary flex-fill";
    btnCancel.textContent = "취소";
    btnCancel.addEventListener("click", () => {
      div.remove();
    });
    grp.appendChild(btnCancel);

    // 저장
    const btnSave = document.createElement("button");
    btnSave.className = "btn btn-primary flex-fill";
    btnSave.textContent = "저장";
    btnSave.disabled = false;
    btnSave.addEventListener("click", () => {
      if (uploadInProgress) return;

      btnSave.disabled = true;
      const form = new FormData();
      form.append("commentNo", comment.commentNo);
      form.append("chalNo", chalNo);
      form.append("reply", textarea.value.trim());
      if (editUploadedUuid) form.append("uuidStr", editUploadedUuid);

      axios
        .post(`${contextPath}/chalComment/updateComment`, form)
        .then((res) => {
          if (res.data === "success") location.reload();
          else {
            alert("수정 실패");
            btnSave.disabled = false;
          }
        })
        .catch(() => {
          alert("서버 오류");
          btnSave.disabled = false;
        });
    });
    grp.appendChild(btnSave);

    div.appendChild(grp);
    card.appendChild(div);
  }

  /* 대댓글 작성 영역 열기 */
  function openRecommentArea(card, parentNo) {
    if (card.querySelector(".right-top-reply-area")) {
      card.querySelector(".right-top-reply-area").classList.toggle("d-none");
      return;
    }

    // 컨테이너
    const div = document.createElement("div");
    div.className = "right-top-reply-area mt-3";

    // textarea
    const textarea = document.createElement("textarea");
    textarea.className = "form-control mb-2";
    textarea.maxLength = 1000;
    textarea.rows = 3;
    div.appendChild(textarea);

    // 썸네일 컨테이너
    const thumbContainer = document.createElement("div");
    thumbContainer.className = "mb-2";
    div.appendChild(thumbContainer);

    // 파일 입력
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.accept = "image/*";
    fileInput.className = "form-control mb-2";
    div.appendChild(fileInput);

    // 내부 상태
    let replyUploadedUuid = null;
    let uploadInProgress = false;

    // 저장 버튼 상태 토글 함수
    function updateReplySubmitState() {
      btnSend.disabled = uploadInProgress;
    }

    // 파일 선택 시: 썸네일 → 업로드 → UUID 저장
    fileInput.addEventListener("change", () => {
      const file = fileInput.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        thumbContainer.innerHTML = `
        <img src="${e.target.result}"
             class="img-thumbnail"
             style="max-width:100px; max-height:100px;">
      `;
      };
      reader.readAsDataURL(file);

      // 공통 업로드 함수 사용
      handleFileUpload(file, {
        onSuccess: (uuid) => {
          replyUploadedUuid = uuid;
        },
        onError: () => {
          // 추가 에러 처리가 필요하면 여기에
        },
        onFinally: () => {
          updateReplySubmitState();
        },
        onProgress: (inProgress) => {
          uploadInProgress = inProgress;
          updateReplySubmitState();
        },
      });
    });

    // 버튼 그룹
    const grp = document.createElement("div");
    grp.className = "d-flex gap-2";

    // 취소 버튼
    const btnCancel = document.createElement("button");
    btnCancel.className = "btn btn-outline-secondary flex-fill";
    btnCancel.textContent = "취소";
    btnCancel.addEventListener("click", () => {
      div.remove();
    });
    grp.appendChild(btnCancel);

    // 작성(전송) 버튼
    const btnSend = document.createElement("button");
    btnSend.className = "btn btn-success flex-fill";
    btnSend.textContent = "작성";
    btnSend.disabled = false;
    btnSend.addEventListener("click", () => {
      if (uploadInProgress) return;

      btnSend.disabled = true;
      const form = new FormData();
      form.append("recommentTarget", parentNo);
      form.append("chalNo", chalNo);
      if (textarea.value.trim().length) {
        form.append("reply", textarea.value.trim());
      }
      if (replyUploadedUuid) {
        form.append("uuidStr", replyUploadedUuid);
      }

      axios
        .post(`${contextPath}/chalComment/insertComment`, form)
        .then((res) => {
          if (res.data === "success") {
            location.reload();
          } else {
            alert("등록 실패");
            btnSend.disabled = false;
          }
        })
        .catch(() => {
          alert("서버 오류");
          btnSend.disabled = false;
        });
    });
    grp.appendChild(btnSend);

    // DOM에 추가
    div.appendChild(grp);
    card.appendChild(div);
  }

  /* 초기 실행 */
  fetchComments(0).then(() => {
    if (hasMoreComments) {
      observer.observe(sentinelEl);
    }
  });

  /* 댓글 등록 후 새로고침 */
  window.addEventListener("commentsUpdated", () => {
    commentListEl.innerHTML = "";
    currentPage = 0;
    hasMoreComments = true;
    fetchComments(0);
  });
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

//이미지 업로드
/**
 * @param {File} file - 업로드할 파일
 * @param {Object} options - 옵션 객체
 * @param {Function} options.onSuccess - 업로드 성공 시 호출될 콜백 (uuid를 매개변수로 받음)
 * @param {Function} options.onError - 업로드 실패 시 호출될 콜백 (error를 매개변수로 받음)
 * @param {Function} options.onFinally - 업로드 완료 시 항상 호출될 콜백
 * @param {Function} options.onProgress - 업로드 진행 상태 변경 시 호출될 콜백 (boolean을 매개변수로 받음)
 */
function handleFileUpload(file, options = {}) {
  const {
    onSuccess = () => {},
    onError = () => {},
    onFinally = () => {},
    onProgress = () => {},
  } = options;

  // 업로드 시작
  onProgress(true);

  // 파일 전송 함수
  const sendFile = (fileToSend, fileName) => {
    const form = new FormData();
    form.append("file", fileToSend, fileName);
    form.append("chalNo", chalNo);
    return axios.post(`${contextPath}/attachment/insertComment`, form);
  };

  // pictureRequired에 따른 업로드 방식 결정
  const promise =
    pictureRequired === "I"
      ? sendFile(file, file.name)
      : resizeImage(file).then((blob) => sendFile(blob, "routine_img.webp"));

  promise
    .then((res) => {
      onSuccess(res.data);
    })
    .catch((error) => {
      // 기본 에러 처리
      if (error.response) {
        const status = error.response.status;
        if (status === 400) {
          new bootstrap.Modal(
            document.getElementById("duplicationModal")
          ).show();
        } else if (status === 500) {
          alert("서버 오류");
        } else {
          alert(`업로드 실패 (오류코드: ${status})`);
        }
      } else {
        alert("업로드 실패(네트워크)");
      }
      onError(error);
    })
    .finally(() => {
      onProgress(false);
      onFinally();
    });
}
