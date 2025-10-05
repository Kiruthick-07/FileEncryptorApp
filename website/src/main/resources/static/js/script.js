/**
 * File Encryptor Web App JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const encryptForm = document.getElementById('encrypt-form');
    const decryptForm = document.getElementById('decrypt-form');
    const encryptFileInput = document.getElementById('encrypt-file');
    const decryptFileInput = document.getElementById('decrypt-file');
    const encryptFilename = document.getElementById('encrypt-filename');
    const decryptFilename = document.getElementById('decrypt-filename');
    const loadingOverlay = document.getElementById('loading');
    const notification = document.getElementById('notification');

    // Update filename display when a file is selected for encryption
    encryptFileInput.addEventListener('change', function() {
        if (this.files.length > 0) {
            encryptFilename.textContent = this.files[0].name;
            encryptFilename.style.color = 'white';
        } else {
            encryptFilename.textContent = 'Choose file to encrypt';
            encryptFilename.style.color = '';
        }
    });

    // Update filename display when a file is selected for decryption
    decryptFileInput.addEventListener('change', function() {
        if (this.files.length > 0) {
            decryptFilename.textContent = this.files[0].name;
            decryptFilename.style.color = 'white';
        } else {
            decryptFilename.textContent = 'Choose file to decrypt';
            decryptFilename.style.color = '';
        }
    });

    // Handle encrypt form submission
    encryptForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!validateForm('encrypt')) {
            return;
        }
        
        const formData = new FormData(encryptForm);
        processFile('/encrypt', formData);
    });

    // Handle decrypt form submission
    decryptForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!validateForm('decrypt')) {
            return;
        }
        
        const formData = new FormData(decryptForm);
        processFile('/decrypt', formData);
    });

    // Switch between encrypt and decrypt tabs
    window.switchTab = function(tab) {
        const encryptTab = document.getElementById('encrypt-tab');
        const decryptTab = document.getElementById('decrypt-tab');
        const encryptContent = document.getElementById('encrypt-content');
        const decryptContent = document.getElementById('decrypt-content');
        
        if (tab === 'encrypt') {
            encryptTab.classList.add('active');
            decryptTab.classList.remove('active');
            encryptContent.classList.add('active');
            decryptContent.classList.remove('active');
        } else {
            encryptTab.classList.remove('active');
            decryptTab.classList.add('active');
            encryptContent.classList.remove('active');
            decryptContent.classList.add('active');
        }
    };

    /**
     * Validate form inputs
     * @param {string} formType - 'encrypt' or 'decrypt'
     * @returns {boolean} - Whether the form is valid
     */
    function validateForm(formType) {
        const fileInput = document.getElementById(formType + '-file');
        const keyInput = document.getElementById(formType + '-key');
        
        if (fileInput.files.length === 0) {
            showNotification('Please select a file', 'error');
            return false;
        }
        
        const maxSizeMB = 10;
        if (fileInput.files[0].size > maxSizeMB * 1024 * 1024) {
            showNotification(`File size must be less than ${maxSizeMB}MB`, 'error');
            return false;
        }
        
        if (keyInput.value.length < 8) {
            showNotification('Secret key must be at least 8 characters long', 'error');
            return false;
        }
        
        return true;
    }

    /**
     * Process file for encryption or decryption
     * @param {string} url - The API endpoint
     * @param {FormData} formData - Form data including file and key
     */
    function processFile(url, formData) {
        showLoading(true);
        
        fetch(url, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.error || 'Unknown error occurred');
                });
            }
            
            // Get filename from response headers if available
            let filename = 'processed_file';
            const contentDisposition = response.headers.get('Content-Disposition');
            if (contentDisposition) {
                const match = contentDisposition.match(/filename="(.+)"/);
                if (match) {
                    filename = match[1];
                }
            }
            
            return response.blob().then(blob => {
                // Create download link and trigger click
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                a.remove();
                
                // Show success message
                const action = url.includes('encrypt') ? 'encrypted' : 'decrypted';
                showNotification(`File ${action} successfully!`, 'success');
            });
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message || 'An error occurred', 'error');
        })
        .finally(() => {
            showLoading(false);
        });
    }

    /**
     * Show or hide loading overlay
     * @param {boolean} show - Whether to show or hide
     */
    function showLoading(show) {
        if (show) {
            loadingOverlay.classList.add('active');
        } else {
            loadingOverlay.classList.remove('active');
        }
    }

    /**
     * Show notification message
     * @param {string} message - Message to display
     * @param {string} type - 'success' or 'error'
     */
    function showNotification(message, type) {
        notification.textContent = message;
        notification.className = 'notification ' + type;
        
        setTimeout(() => {
            notification.className = 'notification';
        }, 5000);
    }
});