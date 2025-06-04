<script>
    import {createEventDispatcher, onMount} from 'svelte';

    export let selectedRequestId;
    export let selectedRequest;

    const dispatch = createEventDispatcher();

    let supplierInput = '';
    let pendingSupplierIds = [];   // UUIDs the user has typed but not yet submitted
    let supplierList = [];         // Array of { id, name, description } already attached to this RFQ
    let errorMessage = '';
    let successMessage = '';
    let submitting = false;

    // Helper: fetch the existing suppliers for this RFQ and populate `supplierList`
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
            // Expecting JSON array: [{ id: 'uuid-...', name: 'Supplier A', description: '...' }, ...]
            const data = await res.json();
            supplierList = data;
        } catch (err) {
            console.error(err);
            errorMessage = 'Could not load existing suppliers.';
        }
    }

    // On component mount, load the current suppliers
    onMount(() => {
        fetchSuppliers();
    });

    function addSupplier() {
        // Clear any prior messages
        errorMessage = '';
        successMessage = '';

        const candidate = supplierInput.trim();
        if (!candidate) {
            errorMessage = 'Supplier UUID cannot be empty.';
            return;
        }

        // Check total (existing + pending) would not exceed 5
        if (supplierList.length + pendingSupplierIds.length >= 5) {
            errorMessage = 'You can add at most 5 suppliers in total.';
            return;
        }

        // Ensure no duplicate in pending
        if (pendingSupplierIds.includes(candidate)) {
            errorMessage = 'This UUID is already in the pending list.';
            return;
        }
        // Also ensure not already attached on the server
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
        // Clear messages
        errorMessage = '';
        successMessage = '';

        if (pendingSupplierIds.length === 0) {
            errorMessage = 'Add at least one supplier before submitting.';
            return;
        }
        // Double-check total count won't exceed 5
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
            // Clear the pending list
            pendingSupplierIds = [];
            // Re-fetch the updated list from server
            await fetchSuppliers();
        } catch (err) {
            console.error(err);
            errorMessage = 'Failed to submit. Please try again.';
        } finally {
            submitting = false;
        }
    }

    function removePending(idx) {
        // Remove one UUID from the pending list
        pendingSupplierIds = pendingSupplierIds.filter((_, i) => i !== idx);
        errorMessage = '';
        successMessage = '';
    }

    function goBack() {
        dispatch('navigate', 'requests');
    }
</script>

<div class="home">
    <h2 class="mb-4">Add Suppliers to Request</h2>
    <p class="text-muted">Request ID: <strong>{selectedRequestId}</strong></p>
    <p class="text-muted">Request Title: <strong>{selectedRequest.title}</strong></p>
    <p class="text-muted">Request Description: <strong>{selectedRequest.description}</strong></p>
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
                        disabled={
                        submitting ||
                        supplierList.length + pendingSupplierIds.length >= 5
                    }
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
                            >
                                &times;
                            </button>
                        </li>
                    {/each}
                </ul>
            </div>
        {/if}

        <button
                type="submit"
                class="btn btn-primary"
                disabled={
                submitting ||
                pendingSupplierIds.length === 0 ||
                supplierList.length + pendingSupplierIds.length > 5
            }
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
    </form>

    {#if errorMessage}
        <div class="alert alert-danger mt-4">{errorMessage}</div>
    {/if}
    {#if successMessage}
        <div class="alert alert-success mt-4">{successMessage}</div>
    {/if}

    <!-- Table of existing suppliers for this RFQ -->
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
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                {#each supplierList as sup}
                    <tr>
                        <td>{sup.id}</td>
                        <td>{sup.name}</td>
                        <td>{sup.description}</td>
                    </tr>
                {/each}
                </tbody>
            </table>
        {/if}
    </div>
</div>
