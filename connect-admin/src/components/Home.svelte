<script>
    let requestTitle = '';
    let requestDescription = '';
    let deliveryLocation = '';
    let productType = '';

    let submitting = false;
    let successMessage = '';
    let errorMessage = '';

    async function handleSubmit(event) {
        event.preventDefault();
        submitting = true;
        successMessage = '';
        errorMessage = '';

        try {
            const res = await fetch('/rfq-service/internal_api/rfqs', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    title: requestTitle,
                    description: requestDescription,
                    delivery_location: deliveryLocation,
                    product_type: productType,
                })
            });

            if (!res.ok) {
                const errText = await res.text();
                throw new Error(`${res.status}: ${errText}`);
            }

            successMessage = 'Your request has been created!';
            requestTitle = '';
            requestDescription = '';
            deliveryLocation = '';
            productType = '';
        } catch (err) {
            console.error(err);
            errorMessage = 'Failed to create request. Please try again.';
        } finally {
            submitting = false;
        }
    }
</script>

<div class="home">
    <h2 class="mb-4">Create a New Request</h2>

    <form on:submit={handleSubmit}>
        <div class="mb-3">
            <label for="title" class="form-label">Title</label>
            <input
                    id="title"
                    type="text"
                    class="form-control"
                    bind:value={requestTitle}
                    required
                    placeholder="Enter request title"
            />
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <textarea
                    id="description"
                    class="form-control"
                    rows="4"
                    bind:value={requestDescription}
                    required
                    placeholder="Enter request description"
            ></textarea>
        </div>

        <div class="mb-3">
            <label for="location" class="form-label">Delivery Location</label>
            <input
                    id="location"
                    type="text"
                    class="form-control"
                    bind:value={deliveryLocation}
                    required
                    placeholder="Enter delivery location"
            />
        </div>

        <div class="mb-3">
            <label for="type" class="form-label">Product Type</label>
            <input
                    id="type"
                    type="text"
                    class="form-control"
                    bind:value={productType}
                    required
                    placeholder="Enter product type"
            />
        </div>

        <button type="submit" class="btn btn-primary" disabled={submitting}>
            {#if submitting}
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                <span class="ms-1">Submitting…</span>
            {:else}
                Submit Request
            {/if}
        </button>
    </form>

    {#if successMessage}
        <div class="alert alert-success mt-4">{successMessage}</div>
    {/if}

    {#if errorMessage}
        <div class="alert alert-danger mt-4">{errorMessage}</div>
    {/if}
</div>
