async function signUp() {

    const user_dto = {
        fname: document.getElementById("fname").value,
        lname: document.getElementById("lname").value,
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
        
        const popup = Notification();

        if (json.success) {
            window.location = "verify-account.html";
            
            popup.success({
                message: json.content
            });
            
        } else {
            document.getElementById("message").innerHTML = json.content;
            
            popup.success({
                message: json.content
            });
        }
    } else {
        console.log("try agin");
    }
}