<script>
    import {onMount} from 'svelte';
    import Header from './components/Header.svelte';
    import Home from './components/Home.svelte';
    import ProductTable from './components/ProductTable.svelte';
    import Requests from './components/Requests.svelte';
    import RequestDetails from './components/RequestDetails.svelte';

    let currentPage = 'home';
    let selectedRequestId = null;

    function syncFromHash() {
        const raw = window.location.hash.slice(1);
        if (!raw) {
            currentPage = 'home';
            selectedRequestId = null;
            return;
        }
        const [page, param] = raw.split('/', 2);

        if (page === 'products' || page === 'requests' || page === 'home') {
            currentPage = page;
            selectedRequestId = null;
        } else if (page === 'requestDetail' && param) {
            currentPage = 'requestDetail';
            selectedRequestId = param;
        } else {
            currentPage = 'home';
            selectedRequestId = null;
        }
    }

    function navigateTo(payload) {
        if (typeof payload === 'string') {
            window.location.hash = payload;
        } else if (payload.page === 'requestDetail' && payload.id) {
            window.location.hash = `requestDetail/${payload.id}`;
        } else {
            window.location.hash = 'home';
        }
    }

    function onNavigateEvent(e) {
        navigateTo(e.detail);
    }

    onMount(() => {
        syncFromHash();
        window.addEventListener('hashchange', syncFromHash);
        return () => window.removeEventListener('hashchange', syncFromHash);
    });
</script>

<Header on:navigate={onNavigateEvent}/>

<main class="container mt-4">
    {#if currentPage === 'home'}
        <Home/>
    {:else if currentPage === 'products'}
        <ProductTable/>
    {:else if currentPage === 'requests'}
        <Requests on:navigate={onNavigateEvent}/>
    {:else if currentPage === 'requestDetail' && selectedRequestId}
        <RequestDetails
                {selectedRequestId}
                on:navigate={onNavigateEvent}
        />
    {/if}
</main>
