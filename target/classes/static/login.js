function register() {
    const fullName = document.getElementById("fullName").value;
    const email = document.getElementById("email_register").value;
    const password = document.getElementById("password_register").value;

    const user = { fullName, email, password };

    fetch("/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    })
    .then(response => response.text())
    .then(result => {
        //document.getElementById("registerResult").innerText = result;
        console.log(result);
        if (result === "Registration successful") {
            const wrapper = document.querySelector(".wrapper");
            wrapper.classList.add("active");
        }
        else {
            const elem = document.getElementById("register_button");
            elem.classList.add('error')
        }
    });
}

function removeError() {
    const elem = document.getElementById("register_button");
    elem.classList.remove('error')
}


function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = { email, password };

    fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    })
    .then(response => response.text())
    .then(result => {
        //document.getElementById("loginResult").innerText = result;
        console.log(result);
        if (result === "Login successful") {
            // Redirect to a different page on successful login
            today = new Date();
            var expire = new Date();
            expire.setTime(today.getTime() + 3600000*24*15);
            
            document.cookie = "email="+email+";path=/" + ";expires="+expire.toUTCString();
            window.location.href = "messages"; // Change "success.html" to your desired URL
        }
    });
}
