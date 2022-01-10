// post-install.js

/**
 * Script to run after npm install
 *
 * Copy selected files to user's directory
 */

'use strict';

const gentlyCopy = require('gently-copy');

// User's local directory and files to copy
const filesToCopy = ['web/sdk/resources', 'web/sdk/images'];
const userPath = process.env.INIT_CWD;

// Moving files to user's local directory
gentlyCopy(filesToCopy, `${userPath}/public/facetec`);
