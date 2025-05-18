<!-- src/components/Home.svelte -->
<script>
    import { onMount } from 'svelte';
    import { createEventDispatcher } from 'svelte';
  
    // form state
    let requestTitle = '';
    let requestDescription = '';
    let deliveryLocation = '';
  
    let submitting = false;
    let successMessage = '';
    let errorMessage = '';
  
    async function handleSubmit(event) {
      event.preventDefault();
      submitting = true;
      successMessage = '';
      errorMessage = '';
  
      try {
        const res = await fetch('/internal_api/request', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            request_title: requestTitle,
            request_description: requestDescription,
            delivery_location: deliveryLocation
          })
        });
  
        if (!res.ok) {
          const errText = await res.text();
          throw new Error(`${res.status}: ${errText}`);
        }
  
        successMessage = 'Your request has been created!';
        // reset form
        requestTitle = '';
        requestDescription = '';
        deliveryLocation = '';
      } catch (err) {
        console.error(err);
        errorMessage = 'Failed to create request. Please try again.';
      } finally {
        submitting = false;
      }
    }
  </script>
  
  <style>
    .home {
      max-width: 600px;
      /* stays left-aligned */
      padding: 1rem;
    }
  
    form {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      align-items: stretch;       /* ← stretch children to full width */
    }
  
    label {
      font-weight: bold;
    }
  
    input,
    textarea {
      width: 100%;                /* ← full width of the form container */
      box-sizing: border-box;     /* ← include padding/border in the width */
      padding: 0.5rem;
      font-size: 1rem;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
  
    button {
      align-self: flex-start;     /* keep the button from stretching */
      padding: 0.5rem 1rem;
      background-color: #1565c0;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
  
    button:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  
    .message {
      margin-top: 1rem;
      text-align: left;
    }
  
    .success {
      color: green;
    }
  
    .error {
      color: red;
    }
  </style>
  
  
  
  <div class="home">
    <h2>Create a New Request</h2>
  
    <form on:submit={handleSubmit}>
      <div>
        <label for="title">Title</label>
        <input
          id="title"
          type="text"
          bind:value={requestTitle}
          required
          placeholder="Enter request title"
        />
      </div>
  
      <div>
        <label for="description">Description</label>
        <textarea
          id="description"
          rows="4"
          bind:value={requestDescription}
          required
          placeholder="Enter request description"
        ></textarea>
      </div>
  
      <div>
        <label for="location">Delivery Location</label>
        <input
          id="location"
          type="text"
          bind:value={deliveryLocation}
          required
          placeholder="Enter delivery location"
        />
      </div>
  
      <button type="submit" disabled={submitting}>
        {submitting ? 'Submitting…' : 'Submit Request'}
      </button>
    </form>
  
    {#if successMessage}
      <div class="message success">{successMessage}</div>
    {/if}
    {#if errorMessage}
      <div class="message error">{errorMessage}</div>
    {/if}
  </div>
  