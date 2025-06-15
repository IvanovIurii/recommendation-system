<script>
    import {createEventDispatcher, onMount} from 'svelte';

    export let selectedRequestId;

    const dispatch = createEventDispatcher();

    let supplierInput = '';
    let pendingSupplierIds = [];
    let supplierList = [];
    let selectedRequest = {};
    let recommendedSuppliers = [];
    let showRecommendations = false;
    let errorMessage = '';
    let successMessage = '';
    let submitting = false;

    async function fetchSuppliers() {
        errorMessage = '';
        try {
            const res = await fetch(
                `/rfq-service/internal_api/rfqs/${selectedRequestId}/suppliers`
            );
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Error ${res.status}: ${text}`);
            }
            supplierList = await res.json();
        } catch (err) {
            console.error(err);
            errorMessage = 'Could not load existing suppliers.';
        }
    }

    async function fetchRequestDetails() {
        errorMessage = '';
        try {
            const res = await fetch(
                `/rfq-service/internal_api/rfqs/${selectedRequestId}`
            );
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Error ${res.status}: ${text}`);
            }
            selectedRequest = await res.json();
        } catch (err) {
            console.error(err);
            errorMessage = 'Could not load existing suppliers.';
        }
    }

    onMount(() => {
        fetchRequestDetails();
        fetchSuppliers();
    });

    function addSupplier() {
        errorMessage = '';
        successMessage = '';

        const candidate = supplierInput.trim();
        if (!candidate) {
            errorMessage = 'Supplier UUID cannot be empty.';
            return;
        }

        if (supplierList.length + pendingSupplierIds.length >= 5) {
            errorMessage = 'You can add at most 5 suppliers in total.';
            return;
        }

        if (pendingSupplierIds.includes(candidate)) {
            errorMessage = 'This UUID is already in the pending list.';
            return;
        }

        if (supplierList.some((s) => s.id === candidate)) {
            errorMessage = 'This supplier is already attached to the RFQ.';
            return;
        }

        const uuidPattern = /^[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}$/;
        if (!uuidPattern.test(candidate)) {
            errorMessage = 'Invalid UUID format.';
            return;
        }

        pendingSupplierIds = [...pendingSupplierIds, candidate];
        supplierInput = '';
    }

    async function handleSubmit(e) {
        e.preventDefault();
        errorMessage = '';
        successMessage = '';

        if (pendingSupplierIds.length === 0) {
            errorMessage = 'Add at least one supplier before submitting.';
            return;
        }

        if (supplierList.length + pendingSupplierIds.length > 5) {
            errorMessage = 'Cannot add more than 5 suppliers in total.';
            return;
        }

        submitting = true;
        try {
            const res = await fetch(
                `/rfq-service/internal_api/rfqs/${selectedRequestId}/suppliers`,
                {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({suppliers: pendingSupplierIds})
                }
            );
            console.log(JSON.stringify({suppliers: pendingSupplierIds}));
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Error ${res.status}: ${text}`);
            }
            successMessage = 'Suppliers successfully added!';
            pendingSupplierIds = [];
            await fetchSuppliers();
        } catch (err) {
            console.error(err);
            errorMessage = 'Failed to submit. Please try again.';
        } finally {
            submitting = false;
        }
    }

    function removePending(idx) {
        pendingSupplierIds = pendingSupplierIds.filter((_, i) => i !== idx);
        errorMessage = '';
        successMessage = '';
    }

    function goBack() {
        dispatch('navigate', 'requests');
    }

    async function loadRecommendations() {
        errorMessage = '';
        successMessage = '';
        showRecommendations = false;
        try {
            const res = await fetch(`/rfq-service/internal_api/rfqs/${selectedRequestId}/suppliers/recommend`);
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Error ${res.status}: ${text}`);
            }
            recommendedSuppliers = await res.json();
            showRecommendations = true;
        } catch (err) {
            console.error(err);
            errorMessage = 'Failed to load recommendations. Please try again.';
        }
    }

    function formatDate(utcString) {
        if (!utcString) return '';
        const d = new Date(utcString);
        return d.toLocaleString();
    }
</script>

<div class="home">
    <h2 class="mb-4">Add Suppliers to Request</h2>
    <p class="text-muted">Request ID: <strong>{selectedRequestId}</strong></p>
    <p class="text-muted">Request Title: <strong>{selectedRequest.title}</strong></p>
    <p class="text-muted">Request Description: <strong>{selectedRequest.description}</strong></p>
    <p class="text-muted">Request Description: <strong>{selectedRequest.type}</strong></p>
    <p class="text-muted">Request Location: <strong>{selectedRequest.deliveryLocation}</strong></p>

    <form on:submit={handleSubmit}>
        <div class="mb-3">
            <label for="supplierId" class="form-label">Add Suppliers (UUID)</label>
            <div class="input-group">
                <input
                        id="supplierId"
                        type="text"
                        class="form-control"
                        bind:value={supplierInput}
                        placeholder="Enter supplier UUID"
                        disabled={supplierList.length + pendingSupplierIds.length >= 5}
                />
                <button
                        type="button"
                        class="btn btn-outline-primary"
                        on:click={addSupplier}
                        disabled={submitting || supplierList.length + pendingSupplierIds.length >= 5}
                >
                    Add
                </button>
            </div>
            <div class="form-text">
                Up to 5 suppliers in total (existing + pending). Each must be a valid UUID.
            </div>
        </div>

        {#if pendingSupplierIds.length > 0}
            <div class="mb-3">
                <label class="form-label">Pending Suppliers to Submit:</label>
                <ul class="list-group">
                    {#each pendingSupplierIds as sup, i}
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            {sup}
                            <button
                                    type="button"
                                    class="btn btn-sm btn-outline-danger"
                                    on:click={() => removePending(i)}
                            >&times;
                            </button>
                        </li>
                    {/each}
                </ul>
            </div>
        {/if}

        <button
                type="submit"
                class="btn btn-primary"
                disabled={submitting || pendingSupplierIds.length === 0 || supplierList.length + pendingSupplierIds.length > 5}
        >
            {#if submitting}
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                <span class="ms-1">Submitting…</span>
            {:else}
                Submit Suppliers
            {/if}
        </button>

        <button
                type="button"
                class="btn btn-secondary ms-2"
                on:click={goBack}
        >
            Back to Requests
        </button>

        <button
                type="button"
                class="btn btn-info float-end"
                on:click={loadRecommendations}
                disabled={submitting}
        >
            Show Recommendations
        </button>
    </form>

    {#if errorMessage}
        <div class="alert alert-danger mt-4">{errorMessage}</div>
    {/if}
    {#if successMessage}
        <div class="alert alert-success mt-4">{successMessage}</div>
    {/if}

    {#if showRecommendations}
        <div class="mt-5">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h4 class="m-0">Recommended Suppliers</h4>
                <button type="button" class="btn btn-sm btn-outline-secondary"
                        on:click={() => showRecommendations = false}>Close
                </button>
            </div>
            {#if recommendedSuppliers.length === 0}
                <p class="text-muted">No recommendations available.</p>
            {:else}
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Supplier ID (TBD)</th>
                        <th>Supplier</th>
                        <th>Product Name</th>
                        <th>Product Description</th>
                        <th>Product Type</th>
                        <th>Product Delivery Area</th>
                        <th>Matching Score</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each recommendedSuppliers as sup}
                        <tr>
                            <td>{sup.supplier_id}</td>
                            <td>{sup.supplier_name}</td>
                            <td>{sup.supplier_product_name}</td>
                            <td>{sup.supplier_product_description}</td>
                            <td>{sup.supplier_product_type}</td>
                            <td>{sup.supplier_delivery_area}</td>
                            <td>{sup.score}</td>
                        </tr>
                    {/each}
                    </tbody>
                </table>
            {/if}
        </div>
    {/if}

    <div class="mt-5">
        <h4>Current Suppliers for This Request</h4>
        {#if supplierList.length === 0}
            <p class="text-muted">No suppliers attached yet.</p>
        {:else}
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Supplier ID</th>
                    <th>Name</th>
                    <th>Created At (Local)</th>
                </tr>
                </thead>
                <tbody>
                {#each supplierList as sup}
                    <tr>
                        <td>{sup.id}</td>
                        <td>{sup.name}</td>
                        <td>{formatDate(sup.created)}</td>
                    </tr>
                {/each}
                </tbody>
            </table>
        {/if}
    </div>
</div>