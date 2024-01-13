function toggleMenu(event) {
    event.preventDefault();
    const subMenu = document.getElementById("subMenu");
    subMenu.classList.toggle("open-menu");
}