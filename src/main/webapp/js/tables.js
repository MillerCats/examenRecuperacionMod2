document.addEventListener("DOMContentLoaded", async () => {
    let token = getCookie("token");
    const modalEditar = new bootstrap.Modal(document.getElementById("modalEditar"));
    const modalCrear = new bootstrap.Modal(document.getElementById("modalCrear"));
    const modalEliminar = new bootstrap.Modal(document.getElementById("modalEliminar"));
    const showModalCrear = document.getElementById("btnShowModalAdd");
    const btnEditar = document.getElementById("btnEditar");
    const btnEliminar = document.getElementById("btnEliminar");
    if (token) {
        await fetchData();
    } else {
        alert("Debe iniciar sesión.");
        window.location.href = "index.html";
    }

    async function fetchData() {
        const response = await fetch("medicos-table", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            }
        }).then(response => response.json());
        if (response.action === "show") {
            console.log(response);
            const data = response.data;
            const tbody = document.getElementById("dataTable");
            tbody.innerHTML = "";
            data.forEach(cliente => {
                const row = renderFila(cliente);
                tbody.appendChild(row);
            });
        } else {
            alert("Debe iniciar sesión");
            window.location.href = "index.html";
        }
    }

    function renderFila(p) {
        const fila = document.createElement("tr");
        fila.innerHTML = `
        <td>${p.codiMedi}</td>
        <td>${p.ndniMedi}</td>
        <td>${p.nombMedi}</td>
        <td>${p.appaMedi}</td>
        <td>${p.apmaMedi}</td>
        <td>${formatDateToDDMMYY(p.fechNaciMedi)}</td>
        <td>${p.logiMedi}</td>
        <td class="text-center">
            <button class="btn btn-sm btn-primary editar-btn">Editar</button>
        </td>
        <td class="text-center">
            <button class="btn btn-sm btn-danger eliminar-btn">Eliminar</button>
        </td>
    `;
        fila.querySelector(".editar-btn").addEventListener("click", () => {
            document.getElementById("uCodi").value = p.codiMedi;
            document.getElementById("uDni").value = p.ndniMedi;
            document.getElementById("uNombre").value = p.nombMedi;
            document.getElementById("uAppater").value = p.appaMedi;
            document.getElementById("uApmater").value = p.apmaMedi;
            let fecha = new Date(p.fechNaciMedi);
            document.getElementById("uFech").value = fecha.toISOString().split('T')[0];
            document.getElementById("uLogi").value = p.logiMedi;
            modalEditar.show();
        });
        fila.querySelector(".eliminar-btn").addEventListener("click", () => {
            document.getElementById("dCod").value = p.codiMedi;
            document.getElementById("dDni").value = p.ndniMedi;
            document.getElementById("dData").value = p.nombMedi + " " + p.appaMedi;
            modalEliminar.show();
        });
        return fila;
    }

    showModalCrear.addEventListener("click", () => {
        modalCrear.show();
        const btnCrear = document.getElementById("btnCrear");
        btnCrear.addEventListener("click", async () => {
            let dni = document.getElementById("cDni").value;
            let nombre = document.getElementById("cNombre").value;
            let appa = document.getElementById("cAppater").value;
            let apma = document.getElementById("cApmater").value;
            let fecha = document.getElementById("cFech").value;
            let logi = document.getElementById("cLogi").value;
            let pass = document.getElementById("cPass").value;
            const data = {dni: dni, nombre: nombre, appa: appa, apma: apma, fecha: fecha, logi: logi, pass: pass};
            const response = await fetch("medicos-table", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(data)
            }).then(response => response.json());
            if (response.result === "created") {
                await fetchData();
                modalCrear.hide();
            } else {
                alter("Error al actualizar");
                console.log(response.result);
            }
        });
    });

    btnEditar.addEventListener("click", async () => {
        const cod = document.getElementById("uCodi").value;
        const dni = document.getElementById("uDni").value;
        const nombre = document.getElementById("uNombre").value;
        const  appa = document.getElementById("uAppater").value;
        const  apma = document.getElementById("uApmater").value;
        const  fecha = document.getElementById("uFech").value;
        const  logi = document.getElementById("uLogi").value;
        const data = {cod: cod, dni: dni, nombre: nombre, appa: appa, apma: apma, fecha: fecha, logi: logi};
        const response = await fetch("medicos-table", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(data)
        }).then(response => response.json());
        if (response.result === "updated") {
            await fetchData();
            modalEditar.hide();
        } else {
            alter("Error al actualizar");
            console.log(response.result);
        }
    });

    btnEliminar.addEventListener("click", async () => {
        const codigo = document.getElementById("dCod").value;
        const response = await fetch("medicos-table", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({codigo: codigo})
        }).then(response => response.json());
        if (response.result === "deleted") {
            await fetchData();
            modalEliminar.hide();
        } else {
            alter("Error al eliminar");
            console.log(response.result);
        }
    });

    function getCookie(nombre) {
        const valor = `; ${document.cookie}`;
        const partes = valor.split(`; ${nombre}=`);
        if (partes.length === 2) {
            return partes.pop().split(';').shift();
        }
    }

    function formatDateToDDMMYY(dateString) {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            console.error("Fecha inválida:", dateString);
            return dateString;
        }
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0'); // getMonth() es 0-indexado
        const year = String(date.getFullYear()).slice(); // Obtener los últimos dos dígitos del año

        return `${day}-${month}-${year}`;
    }
});