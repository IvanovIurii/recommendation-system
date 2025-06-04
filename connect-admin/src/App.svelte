<script>
    import {onMount} from 'svelte';
    import Header from './components/Header.svelte';
    import Home from './components/Home.svelte';
    import ProductTable from './components/ProductTable.svelte';
    import Requests from './components/Requests.svelte';
    import RequestDetails from './components/RequestDetails.svelte';

    // currentPage: 'home' | 'products' | 'requests' | 'requestDetail'
    let currentPage = 'home';

    // when currentPage === 'requestDetail', selectedRequestId holds the UUID
    let selectedRequestId = null;

    // store the full object (so title/description/location are immediately available)
    let selectedRequest = {};

    // Parse window.location.hash → currentPage & selectedRequestId
    function syncFromHash() {
        const raw = window.location.hash.slice(1); // strip leading '#'
        if (!raw) {
            currentPage = 'home';
            selectedRequestId = null;
            return;
        }

        // Could be: 'home', 'products', 'requests', or 'requestDetail/abc-uuid'
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
        // payload = either a string ('home'/'products'/'requests')
        // or { page: 'requestDetail', id: '<UUID>' }
        if (typeof payload === 'string') {
            window.location.hash = payload;
        } else if (payload.page === 'requestDetail' && payload.id) {
            window.location.hash = `requestDetail/${payload.id}`;
        } else {
            window.location.hash = 'home';
        }
    }

    // Handle the dispatched “navigate” event
    function onNavigateEvent(e) {
        const {page: p, id: idParam, request} = e.detail;

        // If we got a full request object, store it now
        if (p === 'requestDetail' && request) {
            selectedRequest = request;
        }

        // Then update the URL hash based on page & id
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
        <!-- Pass the same navigate handler down -->
        <Requests on:navigate={onNavigateEvent}/>
    {:else if currentPage === 'requestDetail' && selectedRequestId}
        <!-- Now pass both the ID and the full request object -->
        <RequestDetails
                {selectedRequestId}
                {selectedRequest}
                on:navigate={onNavigateEvent}
        />
    {/if}
</main>
