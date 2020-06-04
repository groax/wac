function showWeather(lat, long, city) {
    var weatherInfo = 'https://api.openweathermap.org/data/2.5/weather?units=metric&lat=' + lat + '&lon=' + long + '&appid=07ba3c61c3fa33912aff334008274f5c';
    var h3 = document.querySelector("#mycity");
    h3.innerText = "Het weer in " + city;

    let sessionValues = sessionStorage.getItem(city);

    if (sessionValues) {
        let json = JSON.parse(sessionValues);
        let now = new Date().getTime();

        if (json.timeRequested + 600000 > now) {
            showItems(json);
            return;
        }
    }

    fetch(weatherInfo).then(r => r.json()).then(r => {
        showItems(r);
        r.timeRequested = new Date().getTime();
        sessionStorage.setItem(city, JSON.stringify(r));
    });


    function showItems(r) {
        document.querySelector('#tempratuur').innerText = "Tempratuur: " + r.main.temp;
        document.querySelector('#luchtvochtigheid').innerText = "Luchtvochtigheid " + r.main.humidity;
        document.querySelector('#windsnelheid').innerText = "Windsnelheid: " + r.wind.speed;
        document.querySelector('#windrichting').innerText = "Windrichting " + r.wind.deg;
        document.querySelector('#zonsopgang').innerText = "Zonsopgang " + r.sys.sunrise;
        document.querySelector('#zonsondergang').innerText = "Zonsondergang " + r.sys.sunset;
    }
}

function loadCountries() {
    let table = document.querySelector('#countyTable');

    let trs = document.querySelectorAll(".location").forEach(v => {
        table.removeChild(v);
    });

    var jwt = localStorage.getItem("jwt");

    fetch('/restservices/countries', {
        headers: {
            'Authorization': 'Bearer ' + jwt,
        },
    })
        .then(r => r.json())
        .then(r => {
            for (const key in r) {
                let tr = document.createElement("tr");

                tr.classList.add("location");

                //name
                let name = document.createElement("td");

                name.innerHTML = r[key].name;
                tr.appendChild(name);

                //capital
                let capital = document.createElement("td");
                capital.innerHTML = r[key].capital;
                tr.appendChild(capital);

                //regio
                let regio = document.createElement("td")
                regio.innerHTML = r[key].region;
                tr.appendChild(regio);

                //surface
                let surface = document.createElement("td");
                surface.innerHTML = r[key].surface;
                tr.appendChild(surface);

                //population
                let population = document.createElement("td");
                population.innerHTML = r[key].population;
                tr.appendChild(population);

                let edit = document.createElement("td");
                edit.innerHTML = "<a edit='" + r[key].code + "' href=\"/countries/edit/" + r[key].code + "\">edit</a>";
                tr.appendChild(edit);

                let deleter = document.createElement("td");
                deleter.innerHTML = "<a delete=\"" + r[key].code + "\" href=\"/countries/delete/" + r[key].code + "\">delete</a>";
                tr.appendChild(deleter);

                tr.addEventListener('click', function (e) {
                    showWeather(r[key].latitude, r[key].longitude, r[key].capital);
                });

                tr.addEventListener('dblclick', e => {
                    if (name.childNodes[0].nodeName !== "#text") {
                        return;
                    }

                    name.innerHTML = "<input name='name' county='" + r[key].code + "' type='text' value='" + r[key].name + "'>";
                    capital.innerHTML = "<input name='capital' county='" + r[key].code + "' type='text' value='" + r[key].capital + "'>";
                    regio.innerHTML = "<input name='region' county='" + r[key].code + "' type='text' value='" + r[key].region + "'>";
                    surface.innerHTML = "<input name='surface' county='" + r[key].code + "' type='text' value='" + r[key].surface + "'>";
                    population.innerHTML = "<input name='population' county='" + r[key].code + "' type='text' value='" + r[key].population + "'>"

                });

                table.appendChild(tr);
            }

            [...document.querySelectorAll("a[delete]")].forEach((value) => {
                value.addEventListener('click', e => {
                    e.preventDefault();
                    let code = e.target.getAttribute("delete");

                    var jwt = localStorage.getItem("jwt");
                    fetch('/restservices/countries/' + code, {
                        method: "DELETE",
                        headers: {
                            'Authorization': 'Bearer ' + jwt,
                        },
                    }).then(r => {

                    });

                    loadCountries();
                });
            });

            [...document.querySelectorAll("a[edit]")].forEach(v => {
                v.addEventListener('click', e => {
                    e.preventDefault();
                    let inputs = document.querySelectorAll("input[county=\"" + e.srcElement.getAttribute("edit") + "\"]");

                    let code = e.target.getAttribute("edit");

                    var data = new FormData();


                    [...inputs].forEach(v => {
                        data.append(v.name, v.value);
                    });

                    var encrData = new URLSearchParams(data.entries());


                    var jwt = localStorage.getItem("jwt");
                    fetch('/restservices/countries/' + code, {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'Authorization': 'Bearer ' + jwt,
                        },
                        method: "PUT",
                        body: encrData
                    }).then(r => {
                        return Promise.all([r.status, r.json()]);
                    }).then(([s, err]) => {
                        if(s === 200) {
                            document.querySelector("#tableError").innerText = err.amount_updated + ' geupdated';
                            console.log('oke');
                        } else {
                            console.log(err.error);
                        }
                    }).catch(error => {
                        console.log(error);
                    });

                });
            });

        });
}

function initPage() {
    let result = {};
    fetch('https://ipapi.co/json/').then(r => r.json()).then(r => {
        for (key in r) {
            let textNode = document.createElement("label");
            textNode.classList.add('ip');
            textNode.innerText = key + ": " + r[key];
            document.querySelector('#myLocation').appendChild(textNode);
        }
        document.querySelector("#myLocation").addEventListener('click', e => {
            showWeather(r.latitude, r.longitude, r.city);
        });

        showWeather(r.latitude, r.longitude, r.city);

        setInterval(showWeather, 600000, r.latitude, r.longitude, r.city);
    });


    loadCountries();
}

document.querySelector("#addbutton").addEventListener('click', e => {
    e.preventDefault();
    let form = document.querySelector('#addcountryform');
    var formData = new FormData(form);

    var encdata = new URLSearchParams(formData);

    var jwt = localStorage.getItem("jwt");

    fetch('/restservices/countries', {
        headers: {
            'Authorization': 'Bearer ' + jwt,
        },
        method: 'POST',
        body: encdata
    }).then(r => {
        return Promise.all([r.status, r.json()]);
    }).then(([s, err]) => {
        if(s === 200) {
            document.querySelector("#tableError").innerText = err.amount_updated + ' geupdated';
        } else {
            document.querySelector('#addError').innerText = err.error;
        }
    }).catch(error => {
        console.log(error);
    });
});

initPage();


