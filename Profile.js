console.log("Profile.js Loaded");

// URL for fetching profile details
const profileUrl = "http://localhost:40109/shopping";

// Fetch user details and populate the profile page
document.addEventListener("DOMContentLoaded", () => {
  fetchProfileData();
});

function fetchProfileData() {

    const userId = sessionStorage.getItem("USERID"); // Retrieve the user ID from session
    console.log("userId" + userId);
    // if (!userId) {
    //   alert("You are not logged in. Redirecting to login page.");
    //   window.location.href = "login.html";
    //   return;
    // }
  
    const requestData = {
      userId: userId,
      action: "fetchProfile", // Set action explicitly here
    };
  console.log("profile request hits");
    fetch(profileUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestData),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.status === "success") {
          populateProfile(data.profile);
        } else {
          alert("Failed to fetch profile details.");
          console.error(data.message);
        }
      })
      .catch((error) => console.error("Error fetching profile data:", error));
  }
  