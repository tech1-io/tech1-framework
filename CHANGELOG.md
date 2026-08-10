### Changelog [v1.76]
— feat: jbst-server-resource-burner — CPU/RAM load-testing server with start/stop/clean REST API  
— ci: re-enable docker image pushes; release.yml now builds and pushes images itself  
— ci: publish jbst-server-resource-burner docker image; skip its maven package deploy  
— feat: resource-burner growth-speed tuning — start endpoints accept everySeconds/threads/chunkMB; re-start retunes live  
— refactor: assets/docker renamed to be explicitly server-iam scoped (files, compose services/containers/network/volumes)  
— fix: resource-burner CPU logs — sample system CPU load once per growth step (back-to-back getCpuLoad() calls read 0%)  
