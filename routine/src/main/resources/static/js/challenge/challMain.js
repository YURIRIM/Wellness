//---------------------------오른 사이드바----------------------------
document.addEventListener("DOMContentLoaded", function () {
  const centerDiv = document.querySelector(".challenge-list");

  function loadFragment(url) {
    axios
      .get(url)
      .then(function (response) {
        centerDiv.innerHTML = response.data;
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
//--------------------------------------------------------------------
