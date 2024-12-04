console.log("Admin Login Loaded");

let adminUrl = "http://localhost:40109/admin";

let AdminDataToServer = {
    username: null,
    password: null,
    action: "adminLogin", // Action to differentiate admin login
};

angular.module("adminApp", [])
    .controller("adminController", function ($scope) {
        $scope.submit = function () {
            console.log("Admin Login button clicked");
            if (checkAdminInput($scope)) {
                AdminDataToServer.username = $scope.admin.username;
                AdminDataToServer.password = $scope.admin.password;
                sendAdminDataToServer();
            } else {
                println("Invalid input");
            }
        };
    });

function checkAdminInput($scope) {
    if (!$scope.admin.username || !$scope.admin.password) {
        println("Username or password cannot be empty.");
        return false;
    }
    return true;
}

function sendAdminDataToServer() {
    let req = new XMLHttpRequest();
    req.addEventListener("load", adminRequestListener);
    req.open("POST", adminUrl);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify(AdminDataToServer));
    console.log("Sent to server:", JSON.stringify(AdminDataToServer));
}

function adminRequestListener() {
    let jsonObject = JSON.parse(this.responseText);
    console.log("Response from server:", jsonObject);

    if (jsonObject.status === "success") {
        setAdminID(jsonObject.adminId);
        console.log("Setting ADMINID in session storage:", jsonObject.adminId);

        window.location.href = "http://localhost:40109/adminHomepage.html";
    } else {
        println(jsonObject.message || "Login failed");
    }
}

function println(outputStr) {
    const errorElement = document.getElementById("loginErrorMessage");
    if (errorElement) {
        errorElement.textContent = outputStr; // Update the error message
    } else {
        console.error("Error element not found:", outputStr);
    }
}

function setAdminID(adminID) {
    sessionStorage.setItem("ADMINID", adminID.toString());
}
