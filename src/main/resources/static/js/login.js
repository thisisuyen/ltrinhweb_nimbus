document
    .getElementById("btnLogin")
    .addEventListener("click", login);


async function login() {

    const email =
        document
            .getElementById("email")
            .value;

    const password =
        document
            .getElementById("password")
            .value;

    const message =
        document
            .getElementById("message");


    if (!email || !password) {

        message.textContent =
            "Vui lòng nhập email và password.";

        message.classList.remove("d-none");

        return;
    }


    try {

        const response =
            await fetch(
                "/auth/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


        if (!response.ok) {

            message.textContent =
                "Email hoặc password không đúng.";

            message.classList.remove("d-none");

            return;
        }


        const data =
            await response.json();


        // Lưu JWT
        localStorage.setItem(
            "token",
            data.token
        );


        // Sang profile
        window.location.href =
            "/profile.html";


    } catch (error) {

        console.error(error);

        message.textContent =
            "Không kết nối được Server.";

        message.classList.remove("d-none");
    }
}