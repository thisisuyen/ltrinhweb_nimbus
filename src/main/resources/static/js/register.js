document
    .getElementById("btnRegister")
    .addEventListener("click", register);


async function register() {

    const fullName =
        document
            .getElementById("fullName")
            .value;

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


    if (!fullName || !email || !password) {

        showMessage(
            "Vui lòng nhập đầy đủ thông tin.",
            false
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/auth/signup",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        fullName: fullName,
                        email: email,
                        password: password
                    })
                }
            );


        if (!response.ok) {

            let errorText =
                "Đăng ký thất bại.";

            try {

                const data =
                    await response.json();

                if (data.message) {
                    errorText =
                        data.message;
                }

            } catch (e) {
            }

            showMessage(
                errorText,
                false
            );

            return;
        }


        showMessage(
            "Đăng ký thành công. Đang chuyển đến trang đăng nhập...",
            true
        );


        setTimeout(
            function () {

                window.location.href =
                    "/login.html";

            },
            1500
        );


    } catch (error) {

        console.error(error);

        showMessage(
            "Không kết nối được Server.",
            false
        );
    }
}


function showMessage(text, success) {

    const message =
        document
            .getElementById("message");

    message.textContent =
        text;

    message.classList.remove(
        "d-none",
        "alert-success",
        "alert-danger"
    );


    if (success) {

        message.classList.add(
            "alert-success"
        );

    } else {

        message.classList.add(
            "alert-danger"
        );
    }
}