<script>
    import {onMount, createEventDispatcher} from 'svelte';

    let requests = [];
    let loading = true;
    let error = '';
    const dispatch = createEventDispatcher();

    async function fetchRequests() {
        loading = true;
        error = '';
        try {
            const res = await fetch('/rfq-service/internal_api/rfqs');
            if (!res.ok) {
                throw new Error(`Error ${res.status}: ${await res.text()}`);
            }
            const data = await res.json();
            requests = Array.isArray(data) ? data : [];
        } catch (err) {
            console.error(err);
            error = 'Failed to load requests.';
            requests = [];
        } finally {
            loading = false;
        }
    }

    function goToDetail(id) {
        dispatch('navigate', {page: 'requestDetail', id});
    }

    onMount(fetchRequests);
</script>

<div class="mt-4">
    <h2 class="mb-3">All Requests</h2>

    {#if loading}
        <div class="text-center">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading…</span>
            </div>
        </div>
    {:else if error}
        <div class="alert alert-danger">{error}</div>
    {:else if requests.length === 0}
        <div class="alert alert-info">No requests found.</div>
    {:else}
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <colgroup>
                    <col style="width: 20%"/>   <!-- ID -->
                    <col style="width: 20%"/>   <!-- Title -->
                    <col style="width: 40%"/>   <!-- Description -->
                    <col style="width: 20%"/>   <!-- Delivery Location -->
                </colgroup>
                <thead class="bg-primary text-white">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Delivery Location</th>
                </tr>
                </thead>
                <tbody>
                {#each requests as req}
                    <tr on:click={() => goToDetail(req.rfqId)} style="cursor: pointer;">
                        <td>{req.rfqId}</td>
                        <td>{req.title}</td>
                        <td>{req.description}</td>
                        <td>{req.deliveryLocation}</td>
                    </tr>
                {/each}
                </tbody>
            </table>
        </div>
    {/if}
</div>
  