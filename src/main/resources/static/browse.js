let cart = [];
let productsCache = [];

async function loadProducts() {
    try {
        const response = await fetch("/inventory");
        const products = await response.json();

        productsCache = products;

        const container = document.getElementById("product-list");
        container.innerHTML = "";

        products.forEach(p => {
            const div = document.createElement("div");

            div.innerHTML = `
                <h3>${p.name}</h3>
                <p>Price: ${p.price}</p>
                <p>Stock: ${p.stock}</p>
                <button onclick="addToCart('${p.id}')">
                    +
                </button>
            `;

            container.appendChild(div);
        });

    } catch (err) {
        console.error("Failed to load products", err);
    }
}

function toggleCart() {
    const panel = document.getElementById("cart-panel");

    if (panel.style.display === "none") {
        panel.style.display = "block";
        renderCart();
    } else {
        panel.style.display = "none";
    }
}

function renderCart() {
    const container = document.getElementById("cart-items");
    container.innerHTML = "";

    if (cart.length === 0) {
        container.innerHTML = "<p>Cart is empty</p>";
        return;
    }

    cart.forEach(item => {
        const div = document.createElement("div");

        div.innerHTML = `
            <p><b>${item.name}</b></p>
            <p>Price: ${item.price}</p>
            <p>Qty: ${item.quantity}</p>
        `;

        container.appendChild(div);
    });
}

function addToCart(productId) {

    const product = productsCache.find(p => p.id === productId);

    if (!product) {
        console.error("Product not found:", productId);
        return;
    }

    const existing = cart.find(item => item.id === product.id);

    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: 1
        });
    }

    console.log("Cart:", cart);
}

async function checkout() {
    if (cart.length === 0) {
        alert("Cart is empty");
        return;
    }

    const payload = cart.map(item => ({
        productId: item.id,
        quantity: item.quantity
    }));

    const res = await fetch("/checkout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    const order = await res.json();

    alert("Order placed: " + order.id);
    cart = [];
}

loadProducts();