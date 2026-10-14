import {defineConfig} from "cypress";

export default defineConfig({
    e2e: {
        baseUrl: 'http://localhost:5173',
        retries: {
            runMode: 2,
            openMode: 2
        },
        setupNodeEvents(on, config) {
            // implement node event listeners here
        },
    },
});
