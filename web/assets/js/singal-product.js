async function loadProduct() {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {
        const pid = parameters.get("id");
        const response = await fetch("LoadSingleProduct?pid=" + pid);

        if (response.ok) {
            const json = await response.json();

            if (!json.status) {
                window.location = "index.html";

                return;
            }

            const id = json.product.id;

            document.getElementById("image1").src = "product-images/" + id + "/image1.png";

            document.getElementById("title").innerHTML = json.product.title;
            document.getElementById("product-published-on").innerHTML = json.product.date_time;

            document.getElementById("product-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(json.product.price);

            document.getElementById("product-category").innerHTML = json.product.model.category.name;
            document.getElementById("product-model").innerHTML = json.product.model.name;
            document.getElementById("product-quality").innerHTML = json.product.quality.name;
            document.getElementById("product-measure").innerHTML = json.product.measure.name;
            document.getElementById("product-quantity").innerHTML = json.product.qty;
            document.getElementById("product-description").innerHTML = json.product.description;


            document.getElementById("add-to-cart-main").addEventListener(
                    "click",
                    (e) => {
                addToCart(
                        json.product.id,
                        document.getElementById("add-to-cart-qty").value
                        );
                e.preventDefault();
            });

            let productHtml = document.getElementById("similar-product");
            document.getElementById("similar-product-main").innerHTML = "";

            json.productList.forEach(item => {

                let productCloneHtml = productHtml.cloneNode(true);

                productCloneHtml.querySelector("#similar-product-image").src = "product-images/" + item.id + "/image1.png";
                productCloneHtml.querySelector("#similar-product-a1").href = "shop-details.html?id=" + item.id;
                productCloneHtml.querySelector("#similar-product-title").innerHTML = item.title;
                productCloneHtml.querySelector("#similar-product-quality").innerHTML = item.quality.name;
                productCloneHtml.querySelector("#similar-product-price").innerHTML = "Rs." + new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(item.price);
                productCloneHtml.querySelector("#similar-product-category").innerHTML = item.model.category.name;

                productCloneHtml.querySelector("#add-to-cart-similar").addEventListener(
                        "click",
                        (e) => {
                    addToCart(item.id, 1);
                    e.preventDefault();
                });

                document.getElementById("similar-product-main").appendChild(productCloneHtml);

            });

            


        } else {
            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }

}


async function addToCart(id, qty) {

    const response = await fetch(
            "AddToCart?id=" + id + "&qty=" + qty
            );

    if (response.ok) {

        const json = await response.json();

        const popup = Notification();
        popup.setProperty({
            duration: 5000,
            isHidePrev: true
        });

        if (json.success) {

            popup.success({
                message: json.content
            });

        } else {

            popup.error({
                message: json.content
            });

        }

    } else {
        popup.error({
            message: "Unable to process your request"
        });
    }
    
}

