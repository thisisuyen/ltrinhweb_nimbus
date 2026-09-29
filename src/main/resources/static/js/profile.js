const token =
    localStorage.getItem("token");


if (!token) {

    window.location.href =
        "/login.html";
}


document
    .getElementById("token")
    .value = token;


loadProfile();


async function loadProfile() {

    try {

        const response =
            await fetch(
                "/users/me",
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (response.status === 401
                || response.status === 403) {

            localStorage.removeItem(
                "token"
            );

            window.location.href =
                "/login.html";

            return;
        }


        if (!response.ok) {

            showError(
                "Không lấy được thông tin người dùng."
            );

            return;
        }


        const user =
            await response.json();


        document
            .getElementById("id")
            .textContent =
            user.id ?? "";


        document
            .getElementById("fullName")
            .textContent =
            user.fullName ?? "";


        document
            .getElementById("email")
            .textContent =
            user.email ?? "";


        document
            .getElementById("createdAt")
            .textContent =
            user.createdAt ?? "";


    } catch (error) {

        console.error(error);

        showError(
            "Không kết nối được Server."
        );
    }
}


document
    .getElementById("btnUsers")
    .addEventListener(
        "click",
        loadUsers
    );


async function loadUsers() {

    try {

        const response =
            await fetch(
                "/users",
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (response.status === 401
                || response.status === 403) {

            localStorage.removeItem(
                "token"
            );

            window.location.href =
                "/login.html";

            return;
        }


        const users =
            await response.json();


        const tbody =
            document
                .getElementById("users");


        tbody.innerHTML = "";


        users.forEach(
            function (user) {

                const tr =
                    document.createElement(
                        "tr"
                    );

                const idTd =
                    document.createElement(
                        "td"
                    );

                const nameTd =
                    document.createElement(
                        "td"
                    );

                const emailTd =
                    document.createElement(
                        "td"
                    );


                idTd.textContent =
                    user.id;

                nameTd.textContent =
                    user.fullName;

                emailTd.textContent =
                    user.email;


                tr.appendChild(idTd);
                tr.appendChild(nameTd);
                tr.appendChild(emailTd);

                tbody.appendChild(tr);
            }
        );


    } catch (error) {

        console.error(error);

        showError(
            "Không thể tải danh sách user."
        );
    }
}


document
    .getElementById("btnLogout")
    .addEventListener(
        "click",
        function () {

            localStorage.removeItem(
                "token"
            );

            window.location.href =
                "/login.html";
        }
    );


function showError(message) {

    const error =
        document
            .getElementById("error");

    error.textContent =
        message;

    error.classList.remove(
        "d-none"
    );
}