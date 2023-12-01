
async function login() {
    const loginForm = document.getElementById("login_form");

    const loginId = loginForm.login_id.value;
    const loginPassword = loginForm.login_password.value;

    const response = await fetch("http://localhost:8080/api/users/login", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            loginId: loginId,
            loginPassword: loginPassword
        })
    });

    if (response.ok) {
        location.reload();
    }
    else {
        alert("로그인이 실패했습니다.");
    }

    return false;
}

async function logout() {
    const response = await fetch("http://localhost:8080/api/users/logout", {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'}
        }
    );

    if (response.ok) {
        location.reload();
    }
}

const loginForm = document.getElementById("login_form")

loginForm
    .login_password
    .addEventListener("keyup", function (event) {
    const key = event.key || event.keyCode;

    if (key === 'Enter' || key === 13) {
        event.preventDefault();
        loginForm.querySelector(".btn_login").click();
    }
});
