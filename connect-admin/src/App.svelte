<script>
    import Header from './components/Header.svelte';
    import Home from './components/Home.svelte';
    import ProductTable from './components/ProductTable.svelte';
  
    let currentPage = 'home';  // start on Home
  
    // these props get passed into ProductTable
    let page = 0;
    let size = 25;
  
    function handleNav(e) {
      currentPage = e.detail;
    }
  
    function onPageChange(e) {
      page = e.detail;
    }
  </script>
  
  <Header
    title="Supplier Products"
    on:navigate={handleNav}
  />
  
  <main style="padding: 1rem;">
    {#if currentPage === 'home'}
      <Home />
    {:else if currentPage === 'products'}
      <ProductTable {page} {size} on:pageChange={onPageChange} />
    {/if}
  </main>
  