let cart = [];

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

    if (!res.ok) {
        const error = await res.json();
        alert(error.error + "\n" + error.message);
        return;
    }

    const order = await res.json();

    let itemSummary = order.items.map(i =>
        `${i.name} x${i.quantity} (£${i.price})`
    ).join("\n");
    alert(
        "Order placed!\n\n" +
        "Order ID: " + order.id + "\n\n" +
        "Items:\n" + itemSummary + "\n\n" +
        "Total: £" + order.total.toFixed(2) + "\n" +
        "Status: " + order.status + "\n" +
        "Payment ID: " + order.paymentId
    );


    cart = [];
    renderCart();
}

function addToCart(button) {

    const id = button.dataset.id;
    const name = button.dataset.name;
    const price = Number(button.dataset.price);

    const existing = cart.find(item => item.id === id);

    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({
            id,
            name,
            price,
            quantity: 1
        });
    }
    renderCart();
}

function renderCart() {
    const container = document.getElementById("cart-items");
    container.innerHTML = "";

    if (cart.length === 0) {
        container.innerHTML = "<p>Cart is empty</p>";
        updateTotal();
        return;
    }

    cart.forEach(item => {

        const itemTotal = item.price * item.quantity;

        const div = document.createElement("div");
        div.style.border = "1px solid #ddd";
        div.style.margin = "5px";
        div.style.padding = "5px";

        div.innerHTML = `
            <p><b>${item.name}</b></p>
            <p>Qty: ${item.quantity}</p>
            <p>Price: £${item.price.toFixed(2)}</p>
            <p><b>Subtotal: £${itemTotal.toFixed(2)}</b></p>
        `;

        container.appendChild(div);
    });

    updateTotal();
}

function updateTotal() {

    const total = cart.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
    }, 0);

    document.getElementById("cart-total").innerText =
        `Total: £${total.toFixed(2)}`;
}

function toggleCart() {

    const panel = document.getElementById("cart-panel");

    const isHidden = panel.style.display === "none" || panel.style.display === "";

    if (isHidden) {
        panel.style.display = "block";
        renderCart();
    } else {
        panel.style.display = "none";
    }
}