async function checkSignIn() {

    const response = await fetch("CheckSignIn");

    if (response.ok) {

        const json = await response.json();

        const response_DTO = json.response_DTO;

        if (response_DTO.success) {

            //sign in
            const user = response_DTO.content;
            let st_quick_link = document.getElementById("st-quick-link");

            let st_quick_link_li_1 = document.getElementById("st-quick-link-li-1");
            let st_quick_link_li_2 = document.getElementById("st-quick-link-li-2");

            st_quick_link_li_1.remove();
            st_quick_link_li_2.remove();

            let new_li_tag1 = document.createElement("li");
            let new_li_a_tag = document.createElement("a");

            new_li_a_tag.href = "#";
            new_li_a_tag.innerHTML = user.fname + " " + user.lname;
            new_li_tag1.appendChild(new_li_a_tag);
            st_quick_link.appendChild(new_li_tag1);

            let st_button_1 = document.getElementById("st-button-1");
            st_button_1.href = "SignOut";
            st_button_1.innerHTML = "Sign Out";

        } else {

            //not signed in

        }

    }

}

const mainbody = document.getElementById("main-bb");

async function viewCart() {

    mainbody.innerHTML = "";

    document.getElementById("nav-1").remove();

    const response = await fetch("cart.html");

    if (response.ok) {


        const cartHtmlText = await response.text();


        const cartview = new DOMParser();
        const cartHtml = cartview.parseFromString(cartHtmlText, "text/html");

        mainbody.innerHTML = cartHtml.querySelector("#cart-body-1").innerHTML;
        
        loadCartItems();


    }

}