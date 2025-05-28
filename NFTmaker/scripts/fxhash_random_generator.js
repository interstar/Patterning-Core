// Create a constructor for the FX(hash) random generator
function FXHashRandomGenerator() {
  // Create the object with the RandomGenerator protocol methods
  this.randomFloat = function() {
    return fxrand();
  };
  
  this.randomInt = function(n) {
    return Math.floor(fxrand() * n);
  };
  
  this.randomNth = function(coll) {
    // Convert ClojureScript sequence to array if needed
    const arr = Array.isArray(coll) ? coll : Array.from(coll);
    const idx = Math.floor(fxrand() * arr.length);
    return arr[idx];
  };
  
  this.randomAngle = function(seed) {
    return seed + (fxrand() - Math.PI/2) + Math.PI/4;
  };
}

// Create and export a default instance
window.fxhashRandom = new FXHashRandomGenerator(); 