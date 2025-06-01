<script>
  import { onMount } from 'svelte';
  import Header from './components/Header.svelte';
  import Home from './components/Home.svelte';
  import ProductTable from './components/ProductTable.svelte';
  import Requests from './components/Requests.svelte';
  import RequestDetails from './components/RequestDetails.svelte';

  // currentPage: 'home' | 'products' | 'requests' | 'requestDetail'
  let currentPage = 'home';
  // when currentPage === 'requestDetail', selectedRequestId holds the UUID
  let selectedRequestId = null;

  // Parse the hash to set currentPage & selectedRequestId
  function syncFromHash() {
    const raw = window.location.hash.slice(1); // strip leading '#'
    if (!raw) {
      // no hash: default to home
      currentPage = 'home';
      selectedRequestId = null;
      return;
    }

    // Could be: 'home', 'products', 'requests', or 'requestDetail/abc-uuid'
    const [page, param] = raw.split('/', 2);

    if (page === 'products' || page === 'requests' || page === 'home') {
      currentPage = page;
      selectedRequestId = null;
    }
    else if (page === 'requestDetail' && param) {
      currentPage = 'requestDetail';
      selectedRequestId = param;
    }
    else {
      // any unrecognized hash → home
      currentPage = 'home';
      selectedRequestId = null;
      // optionally, clear invalid hash:
      // window.location.hash = 'home';
    }
  }

  // Whenever we want to navigate programmatically, call this:
  function navigateTo(payload) {
    // payload can be a simple string ('home'/'products'/'requests')
    // or an object { page: 'requestDetail', id: '<UUID>' }
    if (typeof payload === 'string') {
      window.location.hash = payload;
    }
    else if (payload.page === 'requestDetail' && payload.id) {
      window.location.hash = `requestDetail/${payload.id}`;
    }
    // for safety, if payload is malformed, go to home
    else {
      window.location.hash = 'home';
    }
  }

  // When header or child components dispatch 'navigate',
  // we just call navigateTo(...) and hashchange logic takes over.
  function onNavigateEvent(e) {
    navigateTo(e.detail);
  }

  // on mount, sync state from current hash, and listen for future hash changes
  onMount(() => {
    syncFromHash();
    window.addEventListener('hashchange', syncFromHash);
    return () => window.removeEventListener('hashchange', syncFromHash);
  });
</script>

<Header on:navigate={onNavigateEvent} />

<main class="container mt-4">
  {#if currentPage === 'home'}
    <Home />
  {:else if currentPage === 'products'}
    <ProductTable />
  {:else if currentPage === 'requests'}
    <!-- Pass navigate event back to App -->
    <Requests on:navigate={onNavigateEvent} />
  {:else if currentPage === 'requestDetail' && selectedRequestId}
    <RequestDetails {selectedRequestId} on:navigate={onNavigateEvent} />
  {/if}
</main>
