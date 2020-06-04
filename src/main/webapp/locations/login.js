document.querySelector("#loginbutton").addEventListener('click', e => {
    e.preventDefault();
    let form = document.querySelector("#loginForm");

    let formData = new FormData(form);
    let encData = new URLSearchParams(formData);

    fetch('/restservices/auth', {
        method: 'POST',
        body: encData,
        // headers: {
        //     // 'Content-Type': 'application/json'
        //     'Content-Type': 'application/x-www-form-urlencoded',
        // },
    }).then(r => {
        return Promise.all([r.status, r.json()]);
    }).then(([s, err]) => {
        if (s === 200) {
            document.querySelector('#loginError').innerText = 'success!';
            localStorage.setItem("jwt", err.jwt);
        } else {
            document.querySelector('#loginError').innerText = err.error;
        }
    }).catch(error => {
        console.log(error);
    });
});