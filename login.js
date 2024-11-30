let url = "http://localhost:40109/shopping";

let DataToServer = {
    username: null,
    password: null,
    action: null,
};

angular.module("myApp", [])
    .controller("myController", function ($scope) {
        $scope.submit = function () {
            console.log("Login button clicked");
            if (checkInput($scope)) {
                DataToServer.username = $scope.user.username;
                DataToServer.password = $scope.user.password;
                DataToServer.action = "login";
                sendDataToServer();
            } else {
                println("Invalid input");
            }
        };

        $scope.signup = function () {
            window.location.href = "http://localhost:40109/signup.html";
        };
    });

function checkInput($scope) {
    if (!$scope.user.username || !$scope.user.password) {
        println("Username or password cannot be empty.");
        return false;
    }
    return true;
}

function sendDataToServer() {
    let req = new XMLHttpRequest();
    req.addEventListener("load", requestListener);
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify(DataToServer));
    console.log("Sent to server:", JSON.stringify(DataToServer));
}

function requestListener() {
    let jsonObject = JSON.parse(this.responseText);
    console.log("Response from server:", jsonObject);

    if (jsonObject.status === "success") {
        setUID(jsonObject.userId);
        window.location.href = "http://localhost:40109/HomePage.html";
    } else {
        println(jsonObject.message || "Login failed");
    }
}

function println(outputStr) {
    const outputElement = document.getElementById("output");
    if (outputElement) {
        outputElement.innerHTML += outputStr + "<br>";
    } else {
        console.log(outputStr);
    }
}

function setUID(UID) {
    sessionStorage.setItem("UID", UID.toString());
}
