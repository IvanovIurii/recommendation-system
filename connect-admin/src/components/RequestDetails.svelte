<script>
    import {createEventDispatcher, onMount} from 'svelte';

    export let selectedRequestId; // passed in from App.svelte

    const dispatch = createEventDispatcher();

    let supplierInput = '';
    let suppliers = [];          // array of added UUIDs
    let errorMessage = '';
    let successMessage = '';
    let submitting = false;

    function addSupplier() {
        errorMessage = '';
        successMessage = '';

        const candidate = supplierInput.trim();
        if (!candidate) {
            errorMessage = 'Supplier UUID cannot be empty.';
            return;
        }
        if (suppliers.includes(candidate)) {
            errorMessage = 'This UUID is already in the list.';
            return;
        }
        if (suppliers.length >= 5) {
            errorMessage = 'You can add at most 5 suppliers.';
            return;
        }

        const uuidPattern = /^[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}$/;
        if (!uuidPattern.test(candidate)) {
            errorMessage = 'Invalid UUID format.';
            return;
        }

        suppliers = [...suppliers, candidate];
        supplierInput = '';
    }

    async function handleSubmit(e) {
        e.preventDefault();
        errorMessage = '';
        successMessage = '';
        if (suppliers.length === 0) {
            errorMessage = 'Add at least one supplier before submitting.';
            return;
        }

        submitting = true;
        try {
            const res = await fetch(
                `/rfq-service/internal_api/rfqs/${selectedRequestId}/suppliers`,
                {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({suppliers})
                }
            );
            if (!res.ok) {
                const text = await res.text();
                throw new Error(`Error ${res.status}: ${text}`);
            }
            successMessage = 'Suppliers successfully added!';
            suppliers = [];
        } catch (err) {
            console.error(err);
            errorMessage = 'Failed to submit. Please try again.';
        } finally {
            submitting = false;
        }
    }

    function goBack() {
        dispatch('navigate', 'requests');
    }
</script>

<div class="home">
    <h2 class="mb-4">Add Suppliers to Request</h2>
    <p class="text-muted">Request ID: <strong>{selectedRequestId}</strong></p>

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
                />
                <button
                        type="button"
                        class="btn btn-outline-primary"
                        on:click={addSupplier}
                        disabled={suppliers.length >= 5}
                >
                    Add
                </button>
            </div>
            <div class="form-text">Up to 5 suppliers. Each must be a valid UUID.</div>
        </div>

        {#if suppliers.length > 0}
            <div class="mb-3">
                <label class="form-label">Current Suppliers:</label>
                <ul class="list-group">
                    {#each suppliers as sup, i}
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            {sup}
                            <button
                                    type="button"
                                    class="btn btn-sm btn-outline-danger"
                                    on:click={() => {
                    suppliers = suppliers.filter((_, idx) => idx !== i);
                    errorMessage = '';
                    successMessage = '';
                  }}
                            >
                                &times;
                            </button>
                        </li>
                    {/each}
                </ul>
            </div>
        {/if}

        <button type="submit" class="btn btn-primary" disabled={submitting}>
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
</div>
  