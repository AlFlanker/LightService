const path = require('path');
//development
module.exports = {
    mode: 'production',
    devtool: 'source-map',
    entry: {
        globalManager:
            path.join(__dirname, 'src', 'main', 'resources', 'static', 'adminTools', 'globalManager.js')
        // map:
        //     path.join(__dirname, 'src', 'main', 'resources', 'static', 'map', 'map.js')
    },
    devServer: {
        contentBase: path.join(__dirname, 'src', 'main', 'resources', 'static', 'production'),
        compress: true,
        port: 8001,
        allowedHosts: [
            'localhost:9000'
        ],
        stats: 'errors-only',
        clientLogLevel: 'error',

    },
    performance: {hints: false},
    output: {
        path: path.join(__dirname, 'src', 'main', 'resources', 'static', 'production'),
        filename: '[name]-min.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                    }
                }
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.(gif|png|jpe?g|svg)$/i,
                use: [
                    'file-loader',
                    {
                        loader: 'image-webpack-loader',
                        options: {
                            mozjpeg: {
                                progressive: true,
                                quality: 65
                            },
                            // optipng.enabled: false will disable optipng
                            optipng: {
                                enabled: false,
                            },
                            pngquant: {
                                quality: '65-90',
                                speed: 4
                            },
                            gifsicle: {
                                interlaced: false,
                            },
                            // the webp option will enable WEBP
                            webp: {
                                quality: 75
                            }
                        }
                    }
                ]
            },
            {
                test: /\.(png|jpg|gif)$/i,
                use: [
                    {
                        loader: 'url-loader',
                        options: {
                            limit: 8192
                        }
                    }
                ]
            }]
    },
    resolve: {
        modules: [
            path.join(__dirname, 'src', 'main', 'resources', 'static', 'adminTools'),
            // path.join(__dirname, 'src', 'main', 'resources', 'static', 'map'),
            path.join(__dirname, 'node_modules'),
        ],
        extensions: ['.html', '.js', '.json', '.scss', '.css','.png']
    }
}