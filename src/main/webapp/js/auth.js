document.addEventListener("DOMContentLoaded", () => {
    const btnLogin = document.getElementById("btnLogin");
    btnLogin.addEventListener("click", async (event) => {
        const dni = document.getElementById("dni").value;
        const pass = document.getElementById("pass").value;
        event.preventDefault();
        const response = await fetch("login-medico", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({dni: dni, pass: pass})
        }).then(response => response.json());
        if (response.result === "ok") {
            console.log(response.token);
            console.log(response);
            setCookie("token", response.token, 7);
            alert(response.result);
            window.location.href = "tables.html";
        } else {
            alert("Credenciales incorrectas");
        }
        console.log(response);
    });

    function setCookie(nombre, valor, dias) {
        const fecha = new Date();
        fecha.setTime(fecha.getTime() + (dias * 24 * 60 * 60 * 1000));
        const expira = "expires=" + fecha.toUTCString();
        document.cookie = nombre + "=" + valor + ";" + expira + ";path=/";
    }
});