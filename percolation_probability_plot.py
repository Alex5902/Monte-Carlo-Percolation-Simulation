import subprocess
import numpy as np
import matplotlib.pyplot as plt
from tqdm import tqdm

# Parameters
N = 20
T = 100  # number of trials per p
p_values = np.linspace(0.3, 0.8, 100)  # range of site vacancy probabilities

percolation_probs = []

for p in tqdm(p_values, desc="Running simulations"):
    successes = 0
    for _ in range(T):
        # Call your Java program here with N and p
        result = subprocess.run(
            ["java", "-cp", ".;algs4.jar;stdlib.jar", "PercolationProbability", str(N), str(p)],
            stdout=subprocess.PIPE,
            text=True
        )
        if result.stdout.strip() == "1":
            successes += 1
    percolation_probs.append(successes / T)

# Plot
plt.figure(figsize=(10, 6))
plt.plot(p_values, percolation_probs, marker='o', color='steelblue')
plt.axvline(x=0.593, linestyle='--', color='gray', label='Theoretical Threshold')
plt.title(f"Percolation Probability Curve (N={N}, T={T})")
plt.xlabel("Site Vacancy Probability (p)")
plt.ylabel("Percolation Probability")
plt.grid(True, linestyle='--', alpha=0.6)
plt.legend()
plt.tight_layout()
plt.savefig("percolation_probability_plot.png")
plt.show()
