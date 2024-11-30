console.log("Profile.js Loaded");

// URL for fetching profile details
const profileUrl = "http://localhost:40109/shopping";

// Fetch user details and populate the profile page
document.addEventListener("DOMContentLoaded", () => {
  fetchProfileData();
});

function fetchProfileData() {
  const userId = sessionStorage.getItem("USERID"); // Retrieve the user ID from session
  console.log("Retrieved userId:", userId);

  if (!userId) {
    alert("You are not logged in. Redirecting to login page.");
    window.location.href = "login.html";
    return;
  }

  const requestData = {
    userId: userId,
    action: "fetchProfile",
  };

  fetch(profileUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch profile details.");
      }
      return response.json();
    })
    .then((data) => {
      if (data.status === "success") {
        console.log("Profile data fetched successfully:", data);
        populateProfile(data.profile);
      } else {
        console.error("Error in response:", data.message);
        alert("Failed to fetch profile details. Redirecting to login.");
        window.location.href = "login.html";
      }
    })
    .catch((error) => {
      console.error("Error fetching profile data:", error);
      alert("An error occurred while loading your profile. Please try again.");
    });
}

function populateProfile(profile) {
  document.getElementById("firstName").innerText = profile.firstName;
  document.getElementById("lastName").innerText = profile.lastName;
  document.getElementById("email").innerText = profile.email;
  document.getElementById("phoneNumber").innerText = profile.phoneNumber;
  document.getElementById("address").innerText = profile.address;
}
