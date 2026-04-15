console.log("JS Loaded");

const BASE_URL = "http://localhost:8080";

async function login() {
  console.log("Login clicked");

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ username, password })
    });

    console.log("Response received", res);

    const data = await res.json();
    console.log("Data:", data);

    if (res.ok) {
      localStorage.setItem("token", data.token);
      alert("Login success");
      window.location.href = "products.html";
    } else {
      alert("Login failed");
    }
  } catch (err) {
    console.error(err);
    alert("Error connecting to backend");
  }
}



async function loadProducts() {
  const res = await fetch(`${BASE_URL}/products`);
  const products = await res.json();

  const container = document.getElementById("products");

  products.forEach(p => {
    const div = document.createElement("div");
    div.innerHTML = `
      <h3>${p.name}</h3>
      <p>${p.price}</p>
      <button onclick="addToCart(${p.id})">Add to Cart</button>
      <hr>
    `;
    container.appendChild(div);
  });
}




async function addToCart(productId) {
  const token = localStorage.getItem("token");

  const res = await fetch(`${BASE_URL}/cart/add`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({
      productId: productId,
      quantity: 1
    })
  });

  if (res.ok) {
    alert("Added to cart");
  } else {
    alert("Error adding to cart");
  }
}


async function loadCart() {
  const token = localStorage.getItem("token");

  const res = await fetch(`${BASE_URL}/cart`, {
    headers: {
      "Authorization": "Bearer " + token
    }
  });

  const items = await res.json();

  const container = document.getElementById("cart");

  items.forEach(item => {
    const div = document.createElement("div");
    div.innerHTML = `
      <p>${item.product.name} - ${item.quantity}</p>
    `;
    container.appendChild(div);
  });
}