<script>
  import { onMount } from 'svelte';
  import Pagination from './Pagination.svelte';

  let products = [];
  let page = 0;
  let size = 10;
  let totalPages = 1;

  async function fetchPage(p = 0) {
    try {
      const res = await fetch(`/supplier-facts-service/internal_api/products?page=${p}&size=${size}`);
      if (!res.ok) {
        console.error('Failed to fetch products', res);
        return;
      }
      const data = await res.json();

      totalPages = data.totalPages > 0 ? data.totalPages : 1;
      products = data.content;
      page = data.pageable.pageNumber;
    } catch (err) {
      console.error(err);
    }
  }

  onMount(() => {
    fetchPage(0);
  });
</script>

<table class="table table-striped table-hover product-table">
  <colgroup>
    <!-- New “Supplier ID” column on the far left -->
    <col style="width: 25%;" />  <!-- Supplier ID -->
    <col style="width: 10%;" />  <!-- Name -->
    <col style="width: 35%;" />  <!-- Description -->
    <col style="width: 5%;" />  <!-- Type -->
    <col style="width: 10%;" />  <!-- Supplier -->
    <col style="width: 5%;" />  <!-- Delivery Areas -->
  </colgroup>

  <thead class="bg-primary text-white">
    <tr>
      <th>Supplier ID</th>
      <th>Name</th>
      <th>Description</th>
      <th>Type</th>
      <th>Supplier</th>
      <th>Delivery Areas</th>
    </tr>
  </thead>
  <tbody>
    {#if products.length === 0}
      <tr>
        <td colspan="6" class="text-center text-muted">
          No products found.
        </td>
      </tr>
    {:else}
      {#each products as prod}
        <tr>
          <td>{prod.id}</td>
          <td>{prod.productName}</td>
          <td>{prod.productDescription}</td>
          <td>{prod.productType}</td>
          <td>{prod.supplierName}</td>
          <td>{prod.deliveryArea.join(', ')}</td>
        </tr>
      {/each}
    {/if}
  </tbody>
</table>

{#if totalPages > 1}
  <Pagination {page} {totalPages} on:changePage={(e) => fetchPage(e.detail)} />
{/if}
