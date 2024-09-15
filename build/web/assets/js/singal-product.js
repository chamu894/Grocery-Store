async function loadProduct() {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("pid")) {
        const pid = parameters.get("pid");
        const response = await fetch("LoadSingleProduct?pid=" + pid);

        if (response.ok) {
            const json = await response.json();
            
            console.log(json);

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
            
            

            

        } else {
//            window.location = "index.html";
console.log("1");
        }

    } else {
//        window.location = "index.html";
console.log("2");
    }

}