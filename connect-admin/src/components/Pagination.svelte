<script>
  import { createEventDispatcher } from 'svelte';

  export let page = 0;          // zero-based
  export let totalPages = 1;    // total number of pages (minimum 1)

  const dispatch = createEventDispatcher();

  function change(to) {
    if (to >= 0 && to < totalPages) {
      dispatch('changePage', to);
    }
  }
</script>

<nav aria-label="Page navigation">
  <ul class="pagination justify-content-center mt-4">
    <li class="page-item {page === 0 ? 'disabled' : ''}">
      <button class="page-link" on:click={() => change(page - 1)} aria-label="Previous">
        <i class="bi bi-chevron-left"></i>
        <span class="visually-hidden">Previous</span>
      </button>
    </li>

    <li class="page-item disabled">
      <span class="page-link">
        Page {page + 1} of {totalPages}
      </span>
    </li>

    <li class="page-item {page + 1 === totalPages ? 'disabled' : ''}">
      <button class="page-link" on:click={() => change(page + 1)} aria-label="Next">
        <span class="visually-hidden">Next</span>
        <i class="bi bi-chevron-right"></i>
      </button>
    </li>
  </ul>
</nav>
