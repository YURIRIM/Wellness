document.addEventListener("DOMContentLoaded", function () {
  //--------Challenge에 사용할 사진을 로컬 저장소에 넣기--------
  const images = [
    {
      url: "/img/challenge-writer-default.webp",
      storageKey: "challengeWriterImage",
      expiryKey: "challengeWriterImageExpiry",
    },
    {
      url: "/img/challenge-default.webp",
      storageKey: "challengeDefaultImage",
      expiryKey: "challengeDefaultImageExpiry",
    },
  ];

  const oneWeekMs = 7 * 24 * 60 * 60 * 1000;
  const now = Date.now();

  images.forEach((image) => {
    //만료되면 나가리
    const expiry = localStorage.getItem(image.expiryKey);
    if (expiry && now > Number(expiry)) {
      localStorage.removeItem(image.storageKey);
      localStorage.removeItem(image.expiryKey);
    }

    // 이미지가 없으면 fetch 후 저장
    if (!localStorage.getItem(image.storageKey)) {
      fetch(image.url)
        .then((response) => response.blob())
        .then((blob) => {
          const reader = new FileReader();
          reader.onloadend = () => {
            localStorage.setItem(image.storageKey, reader.result); // Base64 저장
            localStorage.setItem(image.expiryKey, String(now + oneWeekMs)); // 만료일 저장
          };
          reader.readAsDataURL(blob); // blob → Base64 변환
        });
    }
  });
});
