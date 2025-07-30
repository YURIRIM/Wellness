// 페이지 로딩 시 모든 함수 실행
document.addEventListener("DOMContentLoaded", function () {
  leftTopScript(); // 2사분면
  leftBottomScript(); // 3사분면
  rightScript(); // 오른쪽
});

// 2사분면: 생성한 방 목록
function leftTopScript() {
  axios
    .post(contextPath + "/video-call/createdRoom")
    .then(function (response) {
      const container = document.getElementById("left-top-container");

      if (response.data && response.data.length > 0) {
        response.data.forEach(function (room) {
          const card = createRoomCard(room, "created");
          container.appendChild(card);
        });
      } else {
        container.innerHTML =
          '<div class="text-center text-muted">생성한 방이 없습니다.</div>';
      }
    })
    .catch(function (error) {
      if (error.response && error.response.status === 404) {
        document.getElementById("left-top-container").innerHTML =
          '<div class="text-center text-muted">생성한 방이 없습니다.</div>';
      } else {
        alert("알 수 없는 오류입니다.");
      }
    });
}

// 3사분면: 생성 가능한 방 목록
function leftBottomScript() {
  axios
    .post(contextPath + "/video-call/notCreatedRoom")
    .then(function (response) {
      const container = document.getElementById("left-bottom-container");

      if (response.data && response.data.length > 0) {
        response.data.forEach(function (challenge) {
          const card = createChallengeCard(challenge);
          container.appendChild(card);
        });
      } else {
        container.innerHTML =
          '<div class="text-center text-muted">생성 가능한 방이 없어요</div>';
      }
    })
    .catch(function (error) {
      if (error.response && error.response.status === 404) {
        document.getElementById("left-bottom-container").innerHTML =
          '<div class="text-center text-muted">생성 가능한 방이 없어요</div>';
      } else {
        alert("방을 알 수 없어요");
      }
    });
}

// 오른쪽: 초대받은 방 목록
function rightScript() {
  axios
    .post(contextPath + "/video-call/invitedRoom")
    .then(function (response) {
      const container = document.getElementById("right-container");

      if (response.data && response.data.length > 0) {
        response.data.forEach(function (room) {
          const card = createRoomCard(room, "invited");
          container.appendChild(card);
        });
      } else {
        container.innerHTML =
          '<div class="text-center text-muted">초대받은 방이 없어요!</div>';
      }
    })
    .catch(function (error) {
      if (error.response && error.response.status === 404) {
        document.getElementById("right-container").innerHTML =
          '<div class="text-center text-muted">초대받은 방이 없어요!</div>';
      } else {
        alert("조회 불가!");
      }
    });
}

// VideoCallResponse 카드 생성 함수
function createRoomCard(room, type) {
  const card = document.createElement("div");
  card.className = "card mb-3";

  const cardBody = document.createElement("div");
  cardBody.className = "card-body";

  // 카드 상단: 방 이름과 상태
  const cardHeader = document.createElement("div");
  cardHeader.className =
    "d-flex justify-content-between align-items-center mb-2";

  const roomTitle = document.createElement("h6");
  roomTitle.className = "card-title mb-0";
  if (room.nick) {
    roomTitle.textContent = room.roomName + "(" + room.nick + "님의 방)";
  } else {
    roomTitle.textContent = room.roomName;
  }

  cardHeader.appendChild(roomTitle);

  // 방송 중 표시
  if (room.status === "Y") {
    const liveIndicator = document.createElement("span");
    liveIndicator.className = "badge bg-danger";
    liveIndicator.innerHTML = '<i class="bi bi-broadcast"></i> 방송 중';
    cardHeader.appendChild(liveIndicator);

    // 참여자 수
    const participants = document.createElement("div");
    participants.className = "text-muted small mt-1";
    participants.textContent = room.participants + "명 참여중";
    cardBody.appendChild(participants);
  }

  cardBody.appendChild(cardHeader);

  // 챌린지 제목
  const challengeTitle = document.createElement("p");
  challengeTitle.className = "card-text mb-1";
  challengeTitle.innerHTML = "<strong>" + room.title + "</strong>";
  cardBody.appendChild(challengeTitle);

  // 챌린지 내용
  const challengeContent = document.createElement("p");
  challengeContent.className = "card-text text-muted small mb-2";
  challengeContent.textContent = room.content;
  cardBody.appendChild(challengeContent);

  // 제한 사항과 생성일
  const restrictions = document.createElement("div");
  restrictions.className = "small text-muted mb-2";

  let restrictText = "";
  switch (room.isRestrict) {
    case "N":
      restrictText = "제한 없음";
      break;
    case "Y":
      restrictText = "참여자만";
      break;
    case "S":
      restrictText = "참여자 및 성공한 사람";
      break;
  }

  const createDate = new Date(room.createDate);
  const dateStr =
    createDate.getFullYear() +
    "년 " +
    (createDate.getMonth() + 1) +
    "월 " +
    createDate.getDate() +
    "일";

  restrictions.innerHTML = restrictText + " • " + dateStr;
  cardBody.appendChild(restrictions);

  // 버튼 영역
  const buttonArea = document.createElement("div");
  buttonArea.className = "d-flex gap-2";

  if (type === "created") {
    // 방 열기 버튼
    const openBtn = document.createElement("button");
    openBtn.className = "btn btn-primary btn-sm";
    openBtn.innerHTML = '<i class="bi bi-door-open"></i> 방 열기';
    openBtn.onclick = function () {
      openRoom(room.roomUuidStr);
    };
    buttonArea.appendChild(openBtn);

    // 방 삭제 버튼
    const deleteBtn = document.createElement("button");
    deleteBtn.className = "btn btn-outline-danger btn-sm";
    deleteBtn.innerHTML = '<i class="bi bi-trash"></i> 방 삭제하기';
    deleteBtn.onclick = function () {
      deleteRoom(room.roomUuidStr);
    };
    buttonArea.appendChild(deleteBtn);
  } else if (type === "invited" && room.status === "Y") {
    // 방 참여하기 버튼
    const joinBtn = document.createElement("button");
    joinBtn.className = "btn btn-success btn-sm";
    joinBtn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> 방 참여하기';
    joinBtn.onclick = function () {
      participateRoom(room.roomUuidStr);
    };
    buttonArea.appendChild(joinBtn);
  }

  cardBody.appendChild(buttonArea);
  card.appendChild(cardBody);

  return card;
}

// Challenge 카드 생성 함수
function createChallengeCard(challenge) {
  const card = document.createElement("div");
  card.className = "card mb-3";

  const cardBody = document.createElement("div");
  cardBody.className = "card-body";

  // 챌린지 제목
  const title = document.createElement("h6");
  title.className = "card-title";
  title.textContent = challenge.title;
  cardBody.appendChild(title);

  // 챌린지 내용
  const content = document.createElement("p");
  content.className = "card-text text-muted small mb-3";
  content.textContent = challenge.content;
  cardBody.appendChild(content);

  // 방 생성하기 버튼
  const createBtn = document.createElement("button");
  createBtn.className = "btn btn-outline-primary btn-sm";
  createBtn.innerHTML = '<i class="bi bi-plus-lg"></i> 방 생성하기';
  createBtn.onclick = function () {
    toggleCreateForm(createBtn, challenge.chalNo);
  };
  cardBody.appendChild(createBtn);

  card.appendChild(cardBody);

  return card;
}

// 방 생성 폼 토글
function toggleCreateForm(button, chalNo) {
  const cardBody = button.parentElement;

  // 이미 폼이 있으면 제거
  const existingForm = cardBody.querySelector(".create-room-form");
  if (existingForm) {
    existingForm.remove();
    return;
  }

  // 폼 생성
  const form = document.createElement("div");
  form.className = "create-room-form mt-3 p-3 border rounded bg-light";

  // 방 이름 입력
  const roomNameGroup = document.createElement("div");
  roomNameGroup.className = "mb-3";

  const roomNameLabel = document.createElement("label");
  roomNameLabel.className = "form-label small";
  roomNameLabel.textContent = "방 이름";
  roomNameGroup.appendChild(roomNameLabel);

  const roomNameInput = document.createElement("input");
  roomNameInput.type = "text";
  roomNameInput.name = "roomName";
  roomNameInput.className = "form-control form-control-sm";
  roomNameInput.placeholder = "방 이름을 입력하세요";
  roomNameGroup.appendChild(roomNameInput);

  form.appendChild(roomNameGroup);

  // 참여 제한 선택
  const restrictGroup = document.createElement("div");
  restrictGroup.className = "mb-3";

  const restrictLabel = document.createElement("label");
  restrictLabel.className = "form-label small";
  restrictLabel.textContent = "참여 제한";
  restrictGroup.appendChild(restrictLabel);

  const restrictOptions = [
    { value: "N", text: "제한 없음" },
    { value: "Y", text: "참여자만" },
    { value: "S", text: "성공한 참여자 포함" },
  ];

  restrictOptions.forEach(function (option) {
    const radioDiv = document.createElement("div");
    radioDiv.className = "form-check form-check-inline";

    const radioInput = document.createElement("input");
    radioInput.type = "radio";
    radioInput.name = "isRestrict";
    radioInput.value = option.value;
    radioInput.className = "form-check-input";
    radioInput.id = "restrict-" + option.value + "-" + chalNo;
    if (option.value === "N") radioInput.checked = true; // 기본값

    const radioLabel = document.createElement("label");
    radioLabel.className = "form-check-label small";
    radioLabel.htmlFor = radioInput.id;
    radioLabel.textContent = option.text;

    radioDiv.appendChild(radioInput);
    radioDiv.appendChild(radioLabel);
    restrictGroup.appendChild(radioDiv);
  });

  form.appendChild(restrictGroup);

  // 생성 버튼
  const submitBtn = document.createElement("button");
  submitBtn.className = "btn btn-primary btn-sm";
  submitBtn.innerHTML = '<i class="bi bi-check-lg"></i> 방 생성';
  submitBtn.onclick = function () {
    createRoom(form, chalNo);
  };
  form.appendChild(submitBtn);

  cardBody.appendChild(form);
  roomNameInput.focus();
}

// 방 열기
function openRoom(roomUuidStr) {
  axios
    .post(contextPath + "/video-call/openRoom", { uuidStr: roomUuidStr })
    .then(function (response) {
      if (response.status === 200) {
        window.open(response.data, "_blank");
      }
    })
    .catch(function (error) {
      alert("방에 못 들어가요!");
    });
}

// 방 삭제
function deleteRoom(roomUuidStr) {
  if (!confirm("정말로 방을 삭제하시겠습니까?")) return;

  axios
    .post(contextPath + "/video-call/deleteRoom", { uuidStr: roomUuidStr })
    .then(function (response) {
      if (response.status === 200) {
        // 생성한 방 목록 새로고침
        document.getElementById("left-top-container").innerHTML = "";
        leftTopScript();
      }
    })
    .catch(function (error) {
      alert("함부로 못 지워요!");
    });
}

// 방 생성
function createRoom(form, chalNo) {
  const roomName = form.querySelector('input[name="roomName"]').value.trim();
  const isRestrict = form.querySelector(
    'input[name="isRestrict"]:checked'
  ).value;

  if (!roomName) {
    alert("방 이름을 입력하세요.");
    return;
  }

  const data = {
    chalNo: chalNo,
    roomName: roomName,
    isRestrict: isRestrict,
  };

  axios
    .post(contextPath + "/video-call/createRoom", data)
    .then(function (response) {
      if (response.status === 200) {
        // 폼 제거
        form.remove();

        // 두 목록 모두 새로고침
        document.getElementById("left-top-container").innerHTML = "";
        document.getElementById("left-bottom-container").innerHTML = "";
        leftTopScript();
        leftBottomScript();
      }
    })
    .catch(function (error) {
      alert("생성 대 실 패!");
    });
}

// 방 참여
function participateRoom(roomUuidStr) {
  axios
    .post(contextPath + "/video-call/participateRoom", { uuidStr: roomUuidStr })
    .then(function (response) {
      if (response.status === 200) {
        window.open(response.data, "_blank");
      }
    })
    .catch(function (error) {
      alert("넌 못지나간다");
    });
}
