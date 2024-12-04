function logout() {
    sessionStorage.clear();
    window.location.href = "adminlogin.html";
  }
  
  function navigateTo(page) {
    window.location.href = page;
  }
  