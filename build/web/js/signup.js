async function signUp() {

    const user_dto = {
        fname: document.getElementById("first_name").value,
        lname: document.getElementById("last_name").value,
        mobile: document.getElementById("mobile").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            window.location = "verify-account.html";
        } else {
            document.getElementById("message").innerHTML = json.content;
        }
    } else {
        document.getElementById("message").innerHTML = "Please try agin later";
    }
}