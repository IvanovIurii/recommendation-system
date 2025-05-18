<script>
    import { createEventDispatcher } from 'svelte';
    export let page = 0;
    export let totalPages = 0;
    const dispatch = createEventDispatcher();
  
    function change(p) {
      if (p >= 0 && p < totalPages) {
        dispatch('changePage', p);
      }
    }
  </script>
  
  <nav>
    <button on:click={() => change(page - 1)} disabled={page === 0}>&laquo; Prev</button>
    <span>Page {page + 1} of {totalPages}</span>
    <button on:click={() => change(page + 1)} disabled={page + 1 === totalPages}>Next &raquo;</button>
  </nav>
  
  <style>
    nav { margin-top: 1rem; display: flex; align-items: center; gap: 0.5rem; }
    button { padding: 0.5rem 1rem; }
  </style>