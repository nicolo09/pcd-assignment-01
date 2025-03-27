PCD a.y. 2024-2025 - ISI LM UNIBO - Cesena Campus

# Assignment #01 -  Concurrent Boids

v0.9.0-20250308

The assignment is about designing and developing a concurrent version of the original [boids simulation](https://en.wikipedia.org/wiki/Boids), as conceived by Craig Reynolds in 1986.  

A pseudo-code version of the sequential boids follows:

```
/* constants */
N = number of boids
MAX_SPEED = maximum speed limit for boids
PERCEPTION_RADIUS = distance within which a boid perceives others
AVOID_RADIUS = minimum distance to avoid collisions
SEPARATION_WEIGHT = weight for separation force
ALIGNMENT_WEIGHT = weight for alignment force
COHESION_WEIGHT = weight for cohesion force

/* Boid structure */
Boid {
  position = (x, y)   // 2D point
  velocity = (vx, vy) // 2D vector
}

/* list of boids */
Boid[N] boids - initialized with random positions and velocities

main() {
  while simulation is running {

    /* update boids position */
    for each boid b in boids {

      /* collect neearby boids */
      nearby_boids = collect_nearby_boids(b, boids)
      separation = calculate_separation(b, nearby_boids)
      alignment = calculate_alignment(b, nearby_boids)
      cohesion = calculate_cohesion(b, nearby_boids)

      /* Combine forces and update velocity */
      b.velocity += SEPARATION_WEIGHT * separation
      b.velocity += ALIGNMENT_WEIGHT * alignment
      b.velocity += COHESION_WEIGHT * cohesion

      /* Limit speed to MAX_SPEED */
      if magnitude(b.velocity) > MAX_SPEED
        b.velocity = normalize(b.velocity) * MAX_SPEED      

      /* Update position */
      b.position += b.velocity
    }

    /* render boids */
    render_boids()
  }
}

function collect_nearby_boids(b,boid){
  nearby_boids = []
  for each other_boid in boids {
    if distance(b.position, other_boid.position) < PERCEPTION_RADIUS
      nearby_boids.append(other_boid) 
  }
  return nearby_boids
}

function calculate_separation(boid, nearby_boids){
  force = (0, 0)
  for each other_boid in nearby_boids {
    if distance(boid.position, other_boid.position) < AVOID_RADIUS
      force += normalize(boid.position - other_boid.position)
  }
  return force
}

function calculate_alignment(boid, nearby_boids){
  average_velocity = (0, 0)
  if size(nearby_boids) > 0:
    for each other_boid in nearby_boids 
      average_velocity += other_boid.velocity
    average_velocity /= size(nearby_boids)
    return normalize(average_velocity - boid.velocity)
  } else {
    return (0, 0)
  }
}
 
function calculate_cohesion(boid, nearby_boids){
  center_of_mass = (0, 0)
  if size(nearby_boids) > 0:
    for each other_boid in nearby_boids
      center_of_mass += other_boid.position
    center_of_mass /= size(nearby_boids)
    return normalize(center_of_mass - boid.position) 
  } else {
	return (0, 0)
  }
}
```

A Java-based implementation with a Swing GUI is avaiable in this repo.

The objective of the assignment is to design and develop a concurrent version of Boid, using three different approaches (producing three different versions):
- Java **multithreaded programming** (using default/platform threads)
- **Task-based** approach based o Java **Executor Framework** 
- Java **Virtual Threads** 
    - Remark: *in Virtual Threads based solution, Executors based on Virtual Threads cannot be used*.

The GUI must provide:
- buttons to start, suspend/resume, stop the simulation
- input box to specify at the beginning the number of boids to be used
- sliders to define the weights for separation/alignment/cohesion 

Remarks:
- Every version (multithreaded, task/executor, virtual thread) should exploit as much as possible  the specific key features of the mechanisms/abstractions provided by the approach, both at the design and implementation level. 
- All versions should promote modularity, encapsulation as well as performance, reactivity. 
- For active components/process interaction, prefer the use of higher level constructs (such as monitors) with respecto to lower level (e.g. `synchronized` blocks). 
- A different language than Java can be used: however, in that case, be sure to identify/adopt/implement equivalent frameworks/mechanisms (threads, tasks, virtual threads) for each version.

For every aspect not specified, students are free to choose the best approach for them.

### The deliverable

The deliverable must be a zipped folder `Assignment-01`, to be submitted on the course web site, including:  
- `src` directory with sources
- `doc` directory with a short report in PDF (`report.pdf`). The report should include:
	- A brief analsysis of the problem, focusing in particular aspects that are relevant from concurrent point of view.
	- A description of the adopted design, the strategy and architecture.
	- A description of the behaviour of the system using one or multiple Petri Nets, choosing the propor level of abstraction.
	- Performance tests, to analyse and discuss the performance of the programs (for each version) compared to the sequential version
	- Verification of the program (a model of it) using JPF. For this point, only the  Java multithreaded programming version may be considered.




