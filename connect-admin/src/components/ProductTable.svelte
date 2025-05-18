<script>
    import { onMount } from 'svelte';
    import Pagination from './Pagination.svelte';
  
    let products = [];
    let page = 0;
    let size = 10;
    let totalPages = 0;
  
    async function fetchPage(p = 0) {
      const res = await fetch(`/supplier-facts-service/internal_api/products?page=${p}&size=${size}`);
      if (!res.ok) {
        console.error('Failed to fetch products', res);
        return;
      }
      const data = await res.json();
      products = data.content;
      page = data.pageable.pageNumber;
      totalPages = data.totalPages;
    }
  
    onMount(() => {
      fetchPage();
    });
  </script>
  
  <table>
    <thead>
      <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Type</th>
        <th>Supplier</th>
        <th>Delivery Areas</th>
      </tr>
    </thead>
    <tbody>
      {#each products as prod}
        <tr>
          <td>{prod.productName}</td>
          <td>{prod.productDescription}</td>
          <td>{prod.productType}</td>
          <td>{prod.supplierName}</td>
          <td>{prod.deliveryArea.join(', ')}</td>
        </tr>
      {/each}
    </tbody>
  </table>
  
  <Pagination {page} {totalPages} on:changePage={e => fetchPage(e.detail)} />
  
  <style>
    table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
    th, td { border: 1px solid #ccc; padding: 0.5rem; text-align: left; }
  </style>