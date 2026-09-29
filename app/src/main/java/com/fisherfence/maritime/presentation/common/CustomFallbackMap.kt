package com.fisherfence.maritime.presentation.common

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomFallbackMap(
    modifier: Modifier = Modifier,
    boatLocations: List<Pair<String, Pair<Double, Double>>> = emptyList(),
    boatStatuses: Map<String, String> = emptyMap(),
    showFamilyTrackerLine: Boolean = false,
    familyLocation: Pair<Double, Double> = Pair(13.0827, 80.2707),
    centerLocation: Pair<Double, Double>? = null,
    showHeatmap: Boolean = false,
    heatmapPoints: List<Pair<Pair<Double, Double>, String>> = emptyList(),
    showIMBL: Boolean = true,
    showAvoidZone: Boolean = false,
    interceptionPath: List<Pair<Double, Double>> = emptyList(),
    restrictedZones: Map<String, String> = emptyMap(),
    sarBoatPositions: List<Triple<String, Pair<Double, Double>, Double>> = emptyList(),
    sarDistressedBoat: Pair<Double, Double>? = null,
    showSearchPattern: Boolean = false,
    searchPatternCenter: Pair<Double, Double>? = null,
    showSafeReturnRoute: Boolean = false,
    zoomLevel: Int = 10
) {
    val boatsJson = boatLocations.joinToString(prefix = "[", postfix = "]") { (name, pos) ->
        val status = boatStatuses[name] ?: "SAFE"
        "{ name: '${name.replace("'", "\\'")}', lat: ${pos.first}, lon: ${pos.second}, status: '$status' }"
    }

    val familyJson = if (showFamilyTrackerLine) {
        "{ lat: ${familyLocation.first}, lon: ${familyLocation.second} }"
    } else {
        "null"
    }

    val heatmapJson = heatmapPoints.joinToString(prefix = "[", postfix = "]") { (pos, level) ->
        "{ lat: ${pos.first}, lon: ${pos.second}, level: '$level' }"
    }

    val interceptionJson = interceptionPath.joinToString(prefix = "[", postfix = "]") { pos ->
        "[${pos.first}, ${pos.second}]"
    }

    val zonesJson = restrictedZones.entries.joinToString(prefix = "{", postfix = "}") { entry ->
        "'${entry.key}': '${entry.value}'"
    }

    val sarBoatsJson = sarBoatPositions.joinToString(prefix = "[", postfix = "]") { (name, pos, heading) ->
        "{ name: '${name.replace("'", "\\'")}', lat: ${pos.first}, lon: ${pos.second}, heading: $heading }"
    }

    val distressedJson = if (sarDistressedBoat != null) {
        "[${sarDistressedBoat.first}, ${sarDistressedBoat.second}]"
    } else {
        "null"
    }

    val searchCenterJson = if (searchPatternCenter != null) {
        "[${searchPatternCenter.first}, ${searchPatternCenter.second}]"
    } else {
        "null"
    }

    val centerLat = centerLocation?.first ?: 13.0827
    val centerLon = centerLocation?.second ?: 80.2707

    val htmlContent = remember(centerLat, centerLon, zoomLevel) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map {
                    height: 100%;
                    width: 100%;
                    margin: 0;
                    padding: 0;
                    background-color: #0A0E1A;
                }
                .leaflet-container {
                    background-color: #0A0E1A !important;
                }
                @keyframes pulse-sos {
                    0% { transform: scale(0.5); opacity: 0.9; }
                    100% { transform: scale(2.2); opacity: 0; }
                }
                .sos-pulse-ring {
                    width: 32px;
                    height: 32px;
                    border: 4px solid #FF3B3B;
                    border-radius: 50%;
                    background: rgba(255, 59, 59, 0.2);
                    animation: pulse-sos 1.2s infinite;
                    position: absolute;
                    top: -12px;
                    left: -12px;
                }
                .anchor-svg-container {
                    position: relative;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    zoomControl: false,
                    attributionControl: true
                }).setView([$centerLat, $centerLon], $zoomLevel);

                L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap contributors © CARTO'
                }).addTo(map);

                L.control.zoom({
                    position: 'topright'
                }).addTo(map);

                L.control.scale({
                    position: 'bottomleft'
                }).addTo(map);

                // IMBL boundary polygon Drawn as a red dashed polyline
                var imblCoords = [
                    [13.35, 80.45], [13.20, 80.60], [13.00, 80.65],
                    [12.80, 80.55], [12.70, 80.40], [12.85, 80.25],
                    [13.10, 80.20], [13.35, 80.45]
                ];
                var imblPoly = L.polygon(imblCoords, {
                    color: '#FF3B3B',
                    weight: 3,
                    dashArray: '10, 10',
                    fill: false,
                    interactive: false
                }).addTo(map);

                // Caution zone polygon 5km inside the IMBL
                var cautionCoords = [
                    [13.33, 80.41], [13.19, 80.54], [13.01, 80.59],
                    [12.82, 80.50], [12.72, 80.37], [12.86, 80.23],
                    [13.09, 80.18], [13.33, 80.41]
                ];
                var cautionPoly = L.polygon(cautionCoords, {
                    color: '#FFA500',
                    weight: 2,
                    dashArray: '5, 5',
                    fill: false,
                    interactive: false
                }).addTo(map);

                // Shore baseline marker at Chennai
                L.marker([13.0827, 80.2707], {
                    icon: L.divIcon({
                        html: '<div style="background-color:#00C853; width:12px; height:12px; border-radius:50%; border:2px solid white;"></div>',
                        className: 'shore-dot',
                        iconSize: [12, 12]
                    })
                }).addTo(map).bindPopup("<b>Chennai Shore Baseline</b>");

                var boatGroup = L.layerGroup().addTo(map);
                var pathGroup = L.layerGroup().addTo(map);
                var heatmapGroup = L.layerGroup().addTo(map);
                var sectorsGroup = L.layerGroup().addTo(map);

                var sectorDefs = {
                    'A': [[13.15, 80.20], [13.50, 80.20], [13.50, 80.65], [13.15, 80.65]],
                    'B': [[13.00, 80.25], [13.15, 80.25], [13.15, 80.65], [13.00, 80.65]],
                    'C': [[12.70, 80.20], [13.00, 80.25], [13.00, 80.60], [12.70, 80.50]],
                    'D': [[12.70, 80.50], [13.00, 80.60], [13.50, 80.65], [13.50, 80.85], [12.70, 80.85]]
                };

                function getAnchorSvg(color) {
                    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28" fill="' + color + '"><path d="M12 2a2 2 0 1 1 0 4 2 2 0 0 1 0-4zm1 5h-2v3.18C7.14 10.58 4 13.93 4 18h2c0-3.31 2.69-6 6-6s6 2.69 6 6h2c0-4.07-3.14-7.42-7-7.82V7z"/></svg>';
                }

                window.updateMap = function(boats, family, heatmap, zones, interception, sarBoats, distressed, searchCenter, showReturn) {
                    boatGroup.clearLayers();
                    pathGroup.clearLayers();
                    heatmapGroup.clearLayers();
                    sectorsGroup.clearLayers();

                    for (var key in sectorDefs) {
                        var status = zones[key] || 'OPEN';
                        var color = '#00C853';
                        var fillOpacity = 0.05;
                        if (status === 'RESTRICTED') {
                            color = '#FF3B3B';
                            fillOpacity = 0.25;
                        } else if (status === 'CAUTION') {
                            color = '#FFA500';
                            fillOpacity = 0.15;
                        }

                        L.polygon(sectorDefs[key], {
                            color: color,
                            weight: 2,
                            fillColor: color,
                            fillOpacity: fillOpacity,
                            interactive: false
                        }).addTo(sectorsGroup);
                    }

                    if (heatmap && heatmap.length > 0) {
                        heatmap.forEach(function(p) {
                            var color = '#FF3B3B';
                            var opacity = 0.8;
                            var radius = 2500;
                            if (p.level === 'moderate') {
                                color = '#FFA500';
                                opacity = 0.6;
                                radius = 2000;
                            } else if (p.level === 'light') {
                                color = '#FFA500';
                                opacity = 0.35;
                                radius = 1500;
                            }
                            L.circle([p.lat, p.lon], {
                                radius: radius,
                                color: 'transparent',
                                fillColor: color,
                                fillOpacity: opacity
                            }).addTo(heatmapGroup);
                        });
                    }

                    if (interception && interception.length > 0) {
                        L.polyline(interception, {
                            color: '#FFA500',
                            weight: 3
                        }).addTo(pathGroup);
                    }

                    if (family && boats.length > 0) {
                        var b = boats[0];
                        L.polyline([[family.lat, family.lon], [b.lat, b.lon]], {
                            color: '#00BCD4',
                            weight: 2.5,
                            dashArray: '6, 6'
                        }).addTo(pathGroup);
                    }

                    if (showReturn && boats.length > 0) {
                        var b = boats[0];
                        L.polyline([[13.0827, 80.2707], [b.lat, b.lon]], {
                            color: '#00C853',
                            weight: 2.5,
                            dashArray: '6, 6'
                        }).addTo(pathGroup);
                    }

                    if (searchCenter && searchCenter.length > 0) {
                        var lat = searchCenter[0];
                        var lon = searchCenter[1];
                        var step = 0.005;
                        var pattern = [
                            [lat, lon],
                            [lat, lon + step],
                            [lat - step, lon + step],
                            [lat - step, lon - step],
                            [lat + step, lon - step],
                            [lat + step, lon + 2*step],
                            [lat - 2*step, lon + 2*step],
                            [lat - 2*step, lon - 2*step]
                        ];
                        L.polyline(pattern, {
                            color: '#FFA500',
                            weight: 2.5
                        }).addTo(pathGroup);
                    }

                    boats.forEach(function(b) {
                        var color = '#00BCD4'; // Custom boat marker icon (blue anchor SVG)
                        var isSos = false;
                        if (b.status === 'DANGER') {
                            color = '#FF3B3B';
                        } else if (b.status === 'CAUTION') {
                            color = '#FFA500';
                        } else if (b.status === 'SOS') {
                            color = '#FF3B3B';
                            isSos = true;
                        }

                        var innerHtml = '<div class="anchor-svg-container">';
                        if (isSos) {
                            innerHtml += '<div class="sos-pulse-ring"></div>';
                        }
                        innerHtml += getAnchorSvg(color) + '</div>';

                        var marker = L.marker([b.lat, b.lon], {
                            icon: L.divIcon({
                                html: innerHtml,
                                className: 'custom-boat-marker',
                                iconSize: [28, 28],
                                iconAnchor: [14, 14]
                            })
                        }).addTo(boatGroup);

                        marker.bindPopup("<b>" + b.name + "</b><br>Status: " + b.status + "<br>Location: " + b.lat.toFixed(4) + ", " + b.lon.toFixed(4));
                    });

                    if (sarBoats && sarBoats.length > 0) {
                        sarBoats.forEach(function(cg) {
                            var marker = L.marker([cg.lat, cg.lon], {
                                icon: L.divIcon({
                                    html: '<div style="transform: rotate(' + cg.heading + 'deg);">' +
                                          '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="26" height="26" fill="#B0BEC5"><path d="M12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z"/></svg></div>',
                                    className: 'cg-patrol-marker',
                                    iconSize: [26, 26],
                                    iconAnchor: [13, 13]
                                })
                            }).addTo(boatGroup);
                            marker.bindPopup("<b>" + cg.name + "</b>");
                        });
                    }
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val jsCall = "if (window.updateMap) { window.updateMap($boatsJson, $familyJson, $heatmapJson, $zonesJson, $interceptionJson, $sarBoatsJson, $distressedJson, $searchCenterJson, $showSafeReturnRoute); }"
                        view?.evaluateJavascript(jsCall, null)
                    }
                }
                loadDataWithBaseURL("https://leaflet-map", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            val jsCall = "if (window.updateMap) { window.updateMap($boatsJson, $familyJson, $heatmapJson, $zonesJson, $interceptionJson, $sarBoatsJson, $distressedJson, $searchCenterJson, $showSafeReturnRoute); }"
            webView.evaluateJavascript(jsCall, null)
        }
    )
}
