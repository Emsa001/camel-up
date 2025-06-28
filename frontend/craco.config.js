const path = require('path');

module.exports = {
  webpack: {
    alias: {
      '@src': path.resolve(__dirname, 'src'),
      '@services': path.resolve(__dirname, 'src/services'),
      '@hooks': path.resolve(__dirname, 'src/hooks/index.ts'),
      '@providers': path.resolve(__dirname, 'src/providers/index.tsx'),
      '@components': path.resolve(__dirname, 'src/view/components'),
      '@pages': path.resolve(__dirname, 'src/view/pages'),
      '@layouts': path.resolve(__dirname, 'src/view/layouts'),
      '@view': path.resolve(__dirname, 'src/view'),
    },
  },
};