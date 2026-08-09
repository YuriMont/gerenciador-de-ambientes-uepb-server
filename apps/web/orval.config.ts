import { config } from "dotenv";
import { defineConfig } from "orval";

config();

const openApiUrl = `${process.env.VITE_API_URL}/v3/api-docs`;

export default defineConfig({
  reactQuery: {
    input: openApiUrl,
    output: {
      mode: "tags-split",
      target: "./src/generated/api",
      schemas: "./src/generated/models",
      client: "react-query",
      httpClient: "axios",
      clean: true,
      override: {
        mutator: {
          path: "./src/lib/api.ts",
          name: "api",
        },
      },
    },
    hooks: {
      afterAllFilesWrite: "prettier --write",
    },
  },
  zod: {
    input: openApiUrl,
    output: {
      mode: "tags-split",
      target: "./src/generated/api",
      client: "zod",
      fileExtension: ".zod.ts",
    },
    hooks: {
      afterAllFilesWrite: "prettier --write",
    },
  },
});
