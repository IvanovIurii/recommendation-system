<script>
    import {createEventDispatcher} from 'svelte';

    export let title = "Connect Admin";
    export let navItems = [
        {label: "Home", page: "home"},
        {label: "Products", page: "products"},
        {label: "Requests", page: "requests"}
    ];

    const dispatch = createEventDispatcher();
    let activePage = 'home';

    function navigateTo(page) {
        activePage = page;
        dispatch('navigate', page);
    }
</script>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <span class="navbar-brand mb-0 h1">{title}</span>
        <div class="d-flex gap-3">
            {#each navItems as item}
                <button
                        class="btn btn-link nav-link text-white {item.page === activePage ? 'active fw-bold' : ''}"
                        on:click={() => navigateTo(item.page)}
                >
                    {item.label}
                </button>
            {/each}
        </div>
    </div>
</nav>
