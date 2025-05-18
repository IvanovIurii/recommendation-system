<!-- src/components/Header.svelte -->
<script>
  import { createEventDispatcher } from 'svelte';

  export let title = "My App";
  // now each item has a `page` key we use as our “route”
  export let navItems = [
    { label: "Home", page: "home" },
    // todo: add REQUESTS/
    // each request should select a supplier by ID (todo: add supplier ID to the DB)
    { label: "Products", page: "products" }
  ];

  const dispatch = createEventDispatcher();

  function navigateTo(page) {
    dispatch('navigate', page);
  }
</script>

<style>
  header {
    background-color: #1565c0;
    color: white;
    padding: 0.75rem 1.5rem;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  .logo { font-size: 1.25rem; font-weight: bold; }
  nav { margin-left: auto; display: flex; gap: 1rem; }
  nav a {
    color: white;
    text-decoration: none;
    font-size: 0.95rem;
    cursor: pointer;
    transition: opacity 0.2s;
  }
  nav a:hover { opacity: 0.8; }
</style>

<header>
  <div class="logo">{title}</div>
  <nav>
    {#each navItems as item}
      <a on:click={() => navigateTo(item.page)}>
        {item.label}
      </a>
    {/each}
  </nav>
</header>
