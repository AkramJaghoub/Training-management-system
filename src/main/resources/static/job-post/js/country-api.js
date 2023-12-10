async function fetchCountries() {
    const countrySelect = document.querySelector('#countrySelect');
    const response = await fetch('https://restcountries.com/v2/all');
    const countries = await response.json();

    countries.forEach(country => {
        const option = document.createElement('option');
        option.value = country.alpha2Code;
        option.textContent = country.name;
        countrySelect.appendChild(option);
    });

    countrySelect.addEventListener('change', function() {
        if (this.value) {
            const selectedCountryName = this.options[this.selectedIndex].text;
            fetchCities(this.value);
            document.querySelector('#selectedCountryName').value = selectedCountryName;
        } else {
            document.querySelector('#citySelect').disabled = true;
        }
    });
}

async function fetchCities(countryCode) {
    const citySelect = document.querySelector('#citySelect');
    citySelect.innerHTML = ''; // Clear existing options
    const username = 'kramz';  // Replace with your GeoNames username
    const url = `http://api.geonames.org/searchJSON?country=${countryCode}&maxRows=12&username=${username}&cities=cities1000`;

    try {
        const response = await fetch(url);

        const { geonames } = await response.json();

        console.log(geonames)
        geonames.forEach(city => {
            const option = document.createElement('option');
            option.value = city.toponymName;
            console.log(option.value)
            option.textContent = city.toponymName;
            citySelect.appendChild(option);
        });

        citySelect.disabled = false;
    } catch (error) {
        console.error('Error fetching cities:', error);
        citySelect.disabled = true;
    }
}

fetchCountries();